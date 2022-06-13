package org.joe.cloud.component.transfer.download;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Tainze Zhu
 * @since 2022-06-11
 */
public abstract class Downloader {
    public abstract void download(HttpServletResponse httpServletResponse, String fileUrl);
}
