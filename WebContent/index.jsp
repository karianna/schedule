<%@page import="org.vaadin.devoxx2k10.Configuration"%>
<%@page import="org.vaadin.devoxx2k10.VersionInformation"%>
<%@page import="com.vaadin.terminal.gwt.server.ApplicationServlet"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	String theme = Configuration.getProperty("theme");
	String contextPath = request.getContextPath();
	String vaadinVersion = ApplicationServlet.VERSION;
	String appVersion = VersionInformation.getVersion();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

	<!-- 
		An example of embedding a Vaadin application inside a div.
	-->
	<head>
		<title>Schedule - <%= Configuration.getProperty("conference.name") %></title>
		<style type="text/css">
			body {
				padding: 0;
				margin: 0;
				background: url(devoxx2012-bg.jpg) #000 top center no-repeat !important; 
			}
			#container {
				background: transparent;
				padding-top: 245px;
				width: 1090px;
				height: 1840px;
				margin: 10px auto;
			}
		</style>

	</head>
	
	<body>
		<!-- VAADIN SCRIPTS -->
		<script type="text/javascript">
		//<![CDATA[
		if (!vaadin || !vaadin.vaadinConfigurations) {
			if (!vaadin) { var vaadin = {} } 
			vaadin.vaadinConfigurations = {};
			if (!vaadin.themesLoaded) { vaadin.themesLoaded = {}; }
			vaadin.debug = true;
			document.write('<iframe tabIndex="-1" id="__gwt_historyFrame" style="position:absolute;width:0;height:0;border:0;overflow:hidden;" src="javascript:false"></iframe>');
			document.write("<script language='javascript' src='<%= contextPath %>/VAADIN/widgetsets/org.vaadin.devoxx2k10.widgetset.DevoxxscheduleappWidgetset/org.vaadin.devoxx2k10.widgetset.DevoxxscheduleappWidgetset.nocache.js?1287649373268'><\/script>");
		}
		vaadin.vaadinConfigurations["schedule-wrapper"] = {appUri:'<%= contextPath %>/application', standalone: true, themeUri:'<%= contextPath %>/VAADIN/themes/<%= theme %>', versionInfo : {vaadinVersion:"<%= vaadinVersion %>",applicationVersion:"<%= appVersion %>"},"comErrMsg": {"caption":"Communication problem","message" : "Take note of any unsaved data, and <u>click here<\/u> to continue.","url" : null},"authErrMsg": {"caption":"Authentication problem","message" : "Take note of any unsaved data, and <u>click here<\/u> to continue.","url" : null}};
		//]]>
		</script>
		<script type="text/javascript">
		//<![CDATA[
		if (!vaadin.themesLoaded['<%= theme %>']) {
			var stylesheet = document.createElement('link');
			stylesheet.setAttribute('rel', 'stylesheet');
			stylesheet.setAttribute('type', 'text/css');
			stylesheet.setAttribute('href', '<%= contextPath %>/VAADIN/themes/<%= theme %>/styles.css');
			document.getElementsByTagName('head')[0].appendChild(stylesheet);
			vaadin.themesLoaded['<%= theme %>'] = true;
		}
		//]]>
		</script>
		<script type="text/javascript">
		//<![CDATA[
			setTimeout('if (typeof org_vaadin_devoxx2k10_widgetset_DevoxxscheduleappWidgetset == "undefined") {alert("Failed to load the widgetset: <%= contextPath %>/VAADIN/widgetsets/org.vaadin.devoxx2k10.widgetset.DevoxxscheduleappWidgetset/org.vaadin.devoxx2k10.widgetset.DevoxxscheduleappWidgetset.nocache.js?1287649373268")};',15000);
		//]]>
		</script>
		<!-- /VAADIN SCRIPTS -->
	
		<div id="container">
			<div id="schedule-wrapper" class="v-app v-app-loading v-theme-<%= theme %> v-app-DevoxxScheduleApplication"></div>
		</div>
	</body>
</html>