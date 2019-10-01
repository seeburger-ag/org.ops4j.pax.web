package org.ops4j.pax.web.service.tomcat.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Globals;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * can be based on org.apache.catalina.servlets.DefaultServlet
 *
 * @author Romain Gilles Date: 7/26/12 Time: 10:41 AM
 */
public class TomcatResourceServlet extends HttpServlet {
	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory
			.getLogger(TomcatResourceServlet.class);

	// header constants
	private static final String IF_NONE_MATCH = "If-None-Match";
	private static final String IF_MATCH = "If-Match";
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String IF_RANGE = "If-Range";
	private static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	private static final String KEEP_ALIVE = "Keep-Alive";

	private static final String ETAG = "ETag";

	/**
	 * The input buffer size to use when serving resources.
	 */
	protected int input = 2048;

	private final HttpContext httpContext;
	private final String contextName;
	private final String alias;
	private final String name;

	public TomcatResourceServlet(final HttpContext httpContext,
			final String contextName, final String alias, final String name) {
		this.httpContext = httpContext;
		this.contextName = "/" + contextName;
		this.alias = alias;
		if ("/".equals(name)) {
			this.name = "";
		} else {
			this.name = name;
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (response.isCommitted()) {
			return;
		}

		String mapping = null;
		Boolean included = request
				.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;
		if (included != null && included) {
			String servletPath = (String) request
					.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
			String pathInfo = (String) request
					.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
			if (servletPath == null) {
				servletPath = request.getServletPath();
				pathInfo = request.getPathInfo();
			}
		} else {
			included = Boolean.FALSE;
			if (contextName.equals(alias)) {
				// special handling since resouceServlet has default name
				// attached to it
				if (!"default".equalsIgnoreCase(name)) {
					mapping = name + request.getRequestURI();
				} else {
					mapping = request.getRequestURI();
				}
			} else {
				mapping = request.getRequestURI()
						.replaceFirst(contextName, "/");
				if (!"default".equalsIgnoreCase(name)) {
					mapping = mapping.replaceFirst(alias,
							Matcher.quoteReplacement(name)); // TODO
				}
			}
		}
        if ("//".equals(mapping))
        {
            String[] welcomes = (String[])getServletContext().getAttribute(Globals.WELCOME_FILES_ATTR);
            if (welcomes != null)
            {
                int i; URL url = null;
                for (i = 0; i < welcomes.length && url == null; i++)
                {
                  url = httpContext.getResource(mapping+welcomes[i]);
                  try
                  {
                      if (url != null && url.openConnection() != null)
                      {
                          break;
                      }
                  }
                  catch (IOException ioe) { /* ignore */ }
                }
                if (url != null)
                {
                    response.sendRedirect(request.getRequestURI()+welcomes[i]);
                }
            }
            //don't allow access to '/' as a resource
            if (!response.isCommitted())
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            return;
        }
		URL url = null;
		mapping = normalizePath(mapping);
		if(mapping!=null)
		{
			url = httpContext.getResource(mapping);
		}
		if (url == null
				|| (url != null && "//".equals(mapping) && "bundleentry".equalsIgnoreCase(url.getProtocol()) )
				|| (url != null && "/".equals(mapping)) && "bundleentry".equalsIgnoreCase(url.getProtocol()) ) {
			if (!response.isCommitted()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			return;
		}
		if ("file".equalsIgnoreCase(url.getProtocol())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// For Performanceimprovements turn caching on

		try {
			// new Resource(url.openStream());
			url.openStream();
		} catch (IOException ioex) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		// if the request contains an etag and its the same for the
		// resource, we deliver a NOT MODIFIED response

		// TODO: add lastModified, probably need to use the caching of the
		// DefaultServlet ...
		// set the etag
		// response.setHeader(ETAG, eTag);
		// String mimeType = m_httpContext.getMimeType(mapping);
		String mimeType = getServletContext().getMimeType(url.getFile());
		/*
		 * No Fallback if (mimeType == null) { Buffer mimeTypeBuf =
		 * mimeTypes.getMimeByExtension(mapping); mimeType = mimeTypeBuf != null
		 * ? mimeTypeBuf.toString() : null; }
		 */

		if (mimeType == null) {
			try {
				if (url != null && url.openConnection() != null) {
					mimeType = url.openConnection().getContentType();
				}
			} catch (IOException ignore) {
				// we do not care about such an exception as the fact that
				// we are using also the connection for
				// finding the mime type is just a "nice to have" not an
				// requirement
			} catch (NullPointerException npe) {
				// IGNORE
			}
		}

		if (mimeType == null) {
			ServletContext servletContext = getServletConfig()
					.getServletContext();
			mimeType = servletContext.getMimeType(mapping);
		}

		if (mimeType != null) {
			response.setContentType(mimeType);
		}

		ServletOutputStream out = response.getOutputStream();
		if (out != null) { // null should be just in unit testing
			ServletResponse r = response;
			while (r instanceof ServletResponseWrapper) {
				r = ((ServletResponseWrapper) r).getResponse();
			}
//			if (r instanceof ResponseFacade) {
//				((ResponseFacade) r).getContentWritten();
//			}

			IOException ioException = copyRange(url.openStream(), out);

			if (ioException != null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

		}

	}

	/**
	 * Copy the contents of the specified input stream to the specified output
	 * stream, and ensure that both streams are closed before returning (even in
	 * the face of an exception).
	 *
	 * @param istream
	 *            The input stream to read from
	 * @param ostream
	 *            The output stream to write to
	 * @return Exception which occurred during processing
	 */
	protected IOException copyRange(InputStream istream,
			ServletOutputStream ostream) {

		// first check if the istream is valid
		if (istream == null) {
			return new IOException("Incoming stream is null");
		}

		// Copy the input stream to the output stream
		IOException exception = null;
		byte buffer[] = new byte[input];
		int len = buffer.length;
		while (true) {
			try {
				len = istream.read(buffer);
				if (len == -1) {
					break;
				}
				ostream.write(buffer, 0, len);
			} catch (IOException e) {
				exception = e;
				len = -1;
				break;
			}
		}
		return exception;

	}
	
	private String normalizePath(String path)
	{
		if(path==null)
		{
			return null;
		}
		if(!path.contains(".."))
		{
			return path;
		}
		Path result = Paths.get(path);
		if(result!=null)
		{
			String normalized = result.normalize().toString();
			LOG.debug("Normalized path [{}] to [{}]",path,normalized);
			if(normalized.isEmpty() || normalized.equals("/"))
			{
				//if we are on root level return null
				return null;
			}
			return normalized;
		}
		return path;
	}
}
