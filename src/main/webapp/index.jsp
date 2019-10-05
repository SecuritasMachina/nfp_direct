<%@ page import="org.springframework.data.domain.Pageable" %>
<%@ page import="org.springframework.data.domain.PageRequest" %>
<%@ page import="com.ackdev.nfp.model.Postcard" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ackdev.nfp.PostcardRepository" %>
<%@ page import="org.springframework.data.domain.Page" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="com.ackdev.nfp.PostcardService" %>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.apache.commons.lang3.text.WordUtils" %>
<%@ page import="com.ackdev.nfp.Pub78Service" %>
<%@ page import="com.ackdev.nfp.model.Pub78" %>
<%
    int pageNbr = 0;
    String val = request.getParameter("page");
    if (val != null)
        pageNbr = Integer.parseInt(val);
%>
<html>
<!doctype html>
<html lang="en">
<head>
    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-20653927-31"></script>
    <script>
        window.dataLayer = window.dataLayer || [];

        function gtag() {
            dataLayer.push(arguments);
        }

        gtag('js', new Date());

        gtag('config', 'UA-20653927-31');
    </script>

    <meta charset="utf-8">
    <title>Non for Profit Grant Match List Page <%=pageNbr + 1%>
    </title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
    <link href="assets/css/fontawesome.css" rel="stylesheet">
    <link href="assets/css/brands.css" rel="stylesheet">
    <link href="assets/css/solid.css" rel="stylesheet">

    <!--    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.2/css/all.css" integrity="sha384-fnmOCqbTlWIlj8LyTjo7mOUStjsKC4pOpQbqyi7RrhN7udi9RwhKkMHpvLbHG9Sr" crossorigin="anonymous">-->

    <script src="assets/jquery-3.3.1.slim.min.js"></script>

    <script src="assets/popper.min.js"></script>
    <link rel="stylesheet" href="assets/bootstrap-4.0.0-dist/css/bootstrap.min.css">
    <script src="assets/bootstrap-4.0.0-dist/js/bootstrap.min.js"></script>
    <link rel="apple-touch-icon" sizes="57x57" href="/apple-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="/apple-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="/apple-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="/apple-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="/apple-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="/apple-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="/apple-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="/apple-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="/apple-icon-180x180.png">
    <link rel="icon" type="image/png" sizes="192x192" href="/android-icon-192x192.png">
    <link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="96x96" href="/favicon-96x96.png">
    <link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
    <link rel="manifest" href="/manifest.json">
    <meta name="msapplication-TileColor" content="#ffffff">
    <meta name="msapplication-TileImage" content="/ms-icon-144x144.png">
    <meta name="theme-color" content="#ffffff">

</head>
<body>
<%
    ApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);
    Pub78Service tsc = (Pub78Service) aC.getBean("Pub78Service");
    List<Pub78> Pub78Page = tsc.getPage(pageNbr);

    PostcardService postcardService = (PostcardService) aC.getBean("PostcardService");
%>
<ul class="nav justify-content-center">
    <%if (pageNbr > 0) {%>


    <li class="nav-item">
        <a class="btn btn-outline-primary" href="index.jsp?page=<%=pageNbr-1%>">Previous Page(<%=pageNbr - 1%>)</a>
    </li>
    <%}%>
    <li class="nav-item">
        <a class="btn btn-outline-primary" href="index.jsp?page=<%=pageNbr+1%>">Next Page(<%=pageNbr + 1%>)</a><br/>
    </li>

</ul>
<%
    for (Pub78 pub78 : Pub78Page) {%>
<div class="card">
    <div class="card-body">

        <h5 class="card-title">
            <div class="alert alert-primary" role="alert">
                <%=WordUtils.capitalizeFully(pub78.getCompanyName())%>
            </div>
        </h5>
        <p class="card-text">
        <address>
            <%=pub78.getCity()%> <%=pub78.getCountry()%> <a
                href="details.jsp?id=<%=pub78.getEIN()%>">Details</a>

        </address>
    </div>
</div>
<%
    Postcard postcard = postcardService.findById(pub78.getEIN());
    if (postcard != null) {
%>
<div class="card">
    <div class="card-body">


        <%if (postcard.getDBA1() != null && !postcard.getDBA1().trim().equals("")) {%>
        <h6 class="card-subtitle mb-2 text-muted">DBA: <%=postcard.getDBA1()%>
        </h6>
        <%}%>

        <p class="card-text">
        <address>
            <%=postcard.getOrgAddressLine1()%><br/>
            <%if (postcard.getOrgAddressLine2() != null && !postcard.getOrgAddressLine2().equals("")) {%>
            <%=postcard.getOrgAddressLine2()%><br/>
            <%}%>
            <%=postcard.getOrgCity()%>, <%=postcard.getState()%> <%=postcard.getOrgPostalCode()%><br/>
            <%=postcard.getOrgCountry()%><br/>
        </address>
        </p>

        <%if (postcard.getWebsiteURL() != null && !postcard.getWebsiteURL().equalsIgnoreCase("n/a") && !postcard.getWebsiteURL().trim().equalsIgnoreCase("")) {%>
        <a class="btn btn-outline-primary" target="_blank" href="http://<%=postcard.getWebsiteURL()%>">Website
            @ <%=postcard.getWebsiteURL()%>
        </a>
        <%} else {%>
        No website found <a target="_blank" class="btn btn-outline-primary"
                            href="https://www.google.com/search?q=<%=URLEncoder.encode(postcard.getCompany_name())%>">Try
        Google</a>
        <%}%>
        <br/>
        <a class="btn btn-outline-primary" target="_blank"
           href="https://www.google.com/search?q=<%=URLEncoder.encode(postcard.getCompany_name())%>">Google <%=postcard.getCompany_name()%>
        </a>

    </div>
</div>


</a><br/>
<%
        }
    }
%>
<ul class="nav justify-content-center">
    <%if (pageNbr > 0) {%>


    <li class="nav-item">
        <a class="btn btn-outline-primary" href="index.jsp?page=<%=pageNbr-1%>">Previous Page(<%=pageNbr - 1%>)</a>
    </li>
    <%}%>
    <li class="nav-item">
        <a class="btn btn-outline-primary" href="index.jsp?page=<%=pageNbr+1%>">Next Page(<%=pageNbr + 1%>)</a><br/>
    </li>

</ul>
<a href="https://ackdev.com" target="_blank">Free Non for Profit Lookup Service by AckDev Inc.</a>
</body>
</html>
