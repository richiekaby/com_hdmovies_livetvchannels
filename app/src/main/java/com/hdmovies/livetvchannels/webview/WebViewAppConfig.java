package com.hdmovies.livetvchannels.webview;

import com.hdmovies.livetvchannels.webview.view.PullToRefreshMode;

import static com.hdmovies.livetvchannels.webview.view.PullToRefreshMode.ENABLED;

public class WebViewAppConfig {

	// true for showing html title rather than navigation title in the action bar
	public static final boolean ACTION_BAR_HTML_TITLE = false;

	// true for enabling geolocation
	public static final boolean GEOLOCATION = false;

	// true for enabling progress placeholder when loading a first page
	public static final boolean PROGRESS_PLACEHOLDER = true;

	// pull-to-refresh gesture for refreshing the content,
	// set ENABLED to enable the gesture, set DISABLED to disable the gesture,
	// set PROGRESS to disable the gesture and show only progress indicator
	public static final PullToRefreshMode PULL_TO_REFRESH = ENABLED;

	// custom user agent string for the webview,
	// leave this constant empty if you do not want to set custom user agent string
	public static final String WEBVIEW_USER_AGENT = "";

	// rules for opening links in external browser,
	// if URL link contains the string, it will be opened in external browser,
	// these rules have higher priority than OPEN_LINKS_IN_EXTERNAL_BROWSER option
	public static final String[] LINKS_OPENED_IN_EXTERNAL_BROWSER = {
			"target=blank",
			"target=external"
	};

	// rules for opening links in internal webview,
	// if URL link contains the string, it will be loaded in internal webview,
	// these rules have higher priority than OPEN_LINKS_IN_EXTERNAL_BROWSER option
	public static final String[] LINKS_OPENED_IN_INTERNAL_WEBVIEW = {
			"target=webview",
			"target=internal"
	};

	// list of file extensions or expressions for download,
	// if webview URL matches with this regular expression, the file will be downloaded via download manager,
	// leave this array empty if you do not want to use download manager
	public static final String[] DOWNLOAD_FILE_TYPES = {
			".*zip$", ".*rar$", ".*pdf$", ".*doc$", ".*xls$",
			".*mp3$", ".*wma$", ".*ogg$", ".*m4a$", ".*wav$",
			".*avi$", ".*mov$", ".*mp4$", ".*mpg$", ".*3gp$",
			".*drive.google.com.*file.*",
			".*dropbox.com/s/.*"
	};

	// debug logs, value is set via build config in build.gradle
	public static final boolean LOGS = false;
}
