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
<%@ page import="com.ackdev.nfp.model.Filing" %>
<%@ page import="com.ackdev.nfp.FilingService" %>
<%@ page import="com.ackdev.nfp.Pub78Service" %>
<%@ page import="com.ackdev.nfp.model.Pub78" %>
<%
    String id = request.getParameter("id");
    ApplicationContext aC = RequestContextUtils.findWebApplicationContext(request);

    Pub78Service pub78Service = (Pub78Service) aC.getBean("Pub78Service");
    Pub78 pub78 = pub78Service.findById(id);

    PostcardService postcardService = (PostcardService) aC.getBean("PostcardService");

    Postcard postcard = null;
    try {
        postcard = postcardService.findByName(pub78.getCompanyName());
    } catch (Exception ignore) {
    }

    FilingService filingService = (FilingService) aC.getBean("FilingService");
    Filing filing = null;
    try {
        filing = filingService.findById(pub78.getEIN());
    }catch(Exception i){}

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
    <title>Non for Profit <%=WordUtils.capitalizeFully(pub78.getCompanyName())%>
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
<fmt:setLocale value="en_US"/>
<div class="card">
    <div class="card-body">
        <h5 class="card-title">
            <div class="alert alert-primary" role="alert">
                <%=WordUtils.capitalizeFully(pub78.getCompanyName())%> EIN: <%=id%><a
                    class="btn btn-outline-primary" target="_blank"
                    href="https://news.google.com/search?q=<%=URLEncoder.encode(pub78.getCompanyName()+" " + pub78.getCity())%>">News</a>
            </div>

        </h5>
        <%if (postcard != null) {%>

        <h6 class="card-subtitle mb-2 text-muted"><%=postcard.getYear()%>
        </h6>

        <p class="card-text">
        <address>

            <%=postcard.getOrgAddressLine1()%><br/>
            <%if (postcard.getOrgAddressLine2() != null && !postcard.getOrgAddressLine2().equals("")) {%>
            <%=postcard.getOrgAddressLine2()%><br/>
            <%}%>
            <%=postcard.getOrgCity()%>, <%=postcard.getState()%> <%=postcard.getOrgPostalCode()%><br/>
            <%=postcard.getOrgCountry()%><br/>
        </address>
        <%if (postcard.getDBA1() != null && !postcard.getDBA1().trim().equals("")) {%>
        <h6>Doing Business As:</h6>
        <%=postcard.getDBA1()%> <a target="_blank"
                                   href="https://news.google.com/search?q=<%=URLEncoder.encode(postcard.getDBA1())%>">DBA
        News</a><br/>
        <%=postcard.getDBA2()%><br/>
        <%=postcard.getDBA3()%><br/>
        <%}%>
        <h6>Officer information:</h6>
        <address>
            <a target="_blank"
               href="https://www.google.com/search?q=<%=URLEncoder.encode(postcard.getOfficerName()+" " + postcard.getOfficerAddressCity())%>"><%=postcard.getOfficerName()%>
            </a> <a target="_blank" class="btn btn-outline-primary"
                    href="https://news.google.com/search?q=<%=URLEncoder.encode(postcard.getOfficerName()+" " + postcard.getOfficerAddressCity())%>">News</a><br/>
            <%=postcard.getOfficerAddressLine1()%><br/>
            <%if (postcard.getOfficerAddressLine2() != null && !postcard.getOfficerAddressLine2().trim().equals("")) {%>
            <%=postcard.getOfficerAddressLine2()%><br/>
            <%}%>
            <%=postcard.getOfficerAddressCity()%><br/>

            <%=postcard.getCountry()%><br/>
        </address>

        Gross Receipts Under 25k:  <%=postcard.getGrossReceiptsUnder25k()%><br/>
        Terminated: <%=postcard.getTerminated()%><br/>

        </p>
        <%if (postcard.getWebsiteURL() != null && !postcard.getWebsiteURL().equalsIgnoreCase("n/a") && !postcard.getWebsiteURL().trim().equalsIgnoreCase("")) {%>
        <a class="btn btn-outline-primary" target="_blank" href="http://<%=postcard.getWebsiteURL()%>">Website
            @ <%=postcard.getWebsiteURL()%>
        </a>
        <%} else {%>
        -No website found <a target="_blank" class="btn btn-outline-primary"
                             href="https://www.google.com/search?q=<%=URLEncoder.encode(postcard.getCompany_name())%>">Try
        Google</a>
        <%}%>
        <br/>
        <a target="_blank" class="btn btn-outline-primary"
           href="https://www.google.com/search?q=<%=URLEncoder.encode(postcard.getCompany_name())%>">Google <%=postcard.getCompany_name()%>
        </a>
        <%} else {%>No posting information found<%}%>
    </div>
</div>

<%if (filing != null) {%>
<div class="card">
    <div class="card-body">
        <h5 class="card-title">
            <div class="alert alert-primary" role="alert">
                Filing details
            </div>

        </h5>

        Employer Identification Number: <%=filing.getEIN()%><br/>
        Tax period: <%=filing.getTax_pd()%><br/>
        Subsection code: <%=filing.getSubseccd()%><br/>
        Described in 501(c)(3)?: <%=filing.getS501c3or4947a1cd()%><br/>
        Schedule B required?: <%=filing.getSchdbind()%><br/>
        Political activities?: <%=filing.getPoliticalactvtscd()%><br/>
        Lobbying activities?: <%=filing.getLbbyingactvtscd()%><br/>
        Subject to proxy tax?: <%=filing.getSubjto6033cd()%><br/>
        Donor advised funds?: <%=filing.getDnradvisedfundscd()%><br/>
        Conservation easements?: <%=filing.getPrptyintrcvdcd()%><br/>
        Collections of art?: <%=filing.getMaintwrkofartcd()%><br/>
        Credit counseling?: <%=filing.getCrcounselingqstncd()%><br/>
        Term or permanent endowments?: <%=filing.getHldassetsintermpermcd()%><br/>
        Land buildings and equipment reported?: <%=filing.getRptlndbldgeqptcd()%><br/>
        Investments in other securities reported?: <%=filing.getRptinvstothsecd()%><br/>
        Program related investments reported?: <%=filing.getRptinvstprgrelcd()%><br/>
        Other assets reported?: <%=filing.getRptothasstcd()%><br/>
        Other liabilities reported?: <%=filing.getRptothliabcd()%><br/>
        Separate consolidated financial statement: <%=filing.getSepcnsldtfinstmtcd()%><br/>
        Separate audited financial statement: <%=filing.getSepindaudfinstmtcd()%><br/>
        Included in consolidated financial statements?: <%=filing.getInclinfinstmtcd()%><br/>
        School?: <%=filing.getOperateschools170cd()%><br/>
        Foreign office?: <%=filing.getFrgnofficecd()%><br/>
        Foreign activities, etc?: <%=filing.getFrgnrevexpnscd()%><br/>
        More than $5000 to organizations Part IX, line 3?: <%=filing.getFrgngrntscd()%><br/>
        More than $5000 to individuals Part IX, line 3?: <%=filing.getFrgnaggragrntscd()%><br/>
        Professional fundraising?: <%=filing.getRptprofndrsngfeescd()%><br/>
        Fundraising activities?: <%=filing.getRptincfnndrsngcd()%><br/>
        Gaming?: <%=filing.getRptincgamingcd()%><br/>
        Hospital?: <%=filing.getOperatehosptlcd()%><br/>
        Hospital audited financial statement included?: <%=filing.getHospaudfinstmtcd()%><br/>
        Grants to organizations?: <%=filing.getRptgrntstogovtcd()%><br/>
        Grants to individuals?: <%=filing.getRptgrntstoindvcd()%><br/>
        Schedule J required?: <%=filing.getRptyestocompnstncd()%><br/>
        Tax exempt bonds?: <%=filing.getTxexmptbndcd()%><br/>
        Investment income?: <%=filing.getInvstproceedscd()%><br/>
        Escrow account?: <%=filing.getMaintescrwaccntcd()%><br/>
        On behalf of issuer?: <%=filing.getActonbehalfcd()%><br/>
        Excess benefit transaction?: <%=filing.getEngageexcessbnftcd()%><br/>
        Prior excess benefit transaction?: <%=filing.getAwarexcessbnftcd()%><br/>
        Loan to officer or DQP?: <%=filing.getLoantofficercd()%><br/>
        Grant to related person?: <%=filing.getGrantoofficercd()%><br/>
        Business relationship with organization?: <%=filing.getDirbusnreltdcd()%><br/>
        Business relationship thru family member?: <%=filing.getFmlybusnreltdcd()%><br/>
        Officer, etc. of entity with business relationship?: <%=filing.getServasofficercd()%><br/>
        Deductible non-cash contributions?: <%=filing.getRecvnoncashcd()%><br/>
        Deductible contributions of art, etc?: <%=filing.getRecvartcd()%><br/>
        Terminated?: <%=filing.getCeaseoperationscd()%><br/>
        Partial liquidation?: <%=filing.getSellorexchcd()%><br/>
        Disregarded entity?: <%=filing.getOwnsepentcd()%><br/>
        Related entity?: <%=filing.getReltdorgcd()%><br/>
        Related organization a controlled entity?: <%=filing.getIntincntrlcd()%><br/>
        Any transfers to exempt non-charitable org?: <%=filing.getOrgtrnsfrcd()%><br/>
        Activities conducted thru partnership?: <%=filing.getConduct5percentcd()%><br/>
        Schedule O completed?: <%=filing.getCompltschocd()%><br/>
        Number forms transmitted with 1096: <%=filing.getF1096cnt()%><br/>
        Number W-2Gs included in 1a: <%=filing.getFw2gcnt()%><br/>
        Compliance with backup withholding?: <%=filing.getWthldngrulescd()%><br/>
        Number of employees: <%=filing.getNoemplyeesw3cnt()%><br/>
        Employment tax returns filed?: <%=filing.getFilerqrdrtnscd()%><br/>
        Unrelated business income?: <%=filing.getUnrelbusinccd()%><br/>
        Form 990-T filed?: <%=filing.getFiledf990tcd()%><br/>
        Foreign financial account?: <%=filing.getFrgnacctcd()%><br/>
        Prohibited tax shelter transaction?: <%=filing.getProhibtdtxshltrcd()%><br/>
        Taxable party notification?: <%=filing.getPrtynotifyorgcd()%><br/>
        Form 8886-T filed?: <%=filing.getFiledf8886tcd()%><br/>
        Non-deductible contributions?: <%=filing.getSolicitcntrbcd()%><br/>
        Non-deduct. disclosure?: <%=filing.getExprstmntcd()%><br/>
        Quid pro quo contributions?: <%=filing.getProvidegoodscd()%><br/>
        Quid pro quo disclosure?: <%=filing.getNotfydnrvalcd()%><br/>
        Form 8282 property disposed of?: <%=filing.getFiledf8282cd()%><br/>
        Number of 8282s filed: <%=filing.getF8282cnt()%><br/>
        Funds to pay premiums?: <%=filing.getFndsrcvdcd()%><br/>
        Premiums paid?: <%=filing.getPremiumspaidcd()%><br/>
        Form 8899 filed?: <%=filing.getFiledf8899cd()%><br/>
        Form 1098-C filed?: <%=filing.getFiledf1098ccd()%><br/>
        Excess business holdings?: <%=filing.getExcbushldngscd()%><br/>
        Taxable distributions?: <%=filing.getS4966distribcd()%><br/>
        Distribution to donor?: <%=filing.getDistribtodonorcd()%><br/>
        Initiation fees amount: <%=filing.getInitiationfees()%><br/>
        Gross receipts amount: <%=filing.getGrsrcptspublicuse()%><br/>
        Gross income from members: <%=filing.getGrsincmembers()%><br/>
        Gross income from other sources: <%=filing.getGrsincother()%><br/>
        Form 990 in lieu of 1041?: <%=filing.getFiledlieuf1041cd()%><br/>
        Tax exempt interest in lieu of 1041: <%=filing.getTxexmptint()%><br/>
        Qualified health plan in multiple states: <%=filing.getQualhlthplncd()%><br/>
        Qualified health plan reserves required: <%=filing.getQualhlthreqmntn()%><br/>
        Qualified health plan reserves on hand: <%=filing.getQualhlthonhnd()%><br/>
        Payments for indoor tanning: <%=filing.getRcvdpdtngcd()%><br/>
        Filed Form 720 for tanning: <%=filing.getFiledf720cd()%><br/>
        Reportable compensation from organization: <%=filing.getTotreprtabled()%><br/>
        Reportable compensation from related orgs: <%=filing.getTotcomprelatede()%><br/>
        Other compensation: <%=filing.getTotestcompf()%><br/>
        Number individuals greater than $100K: <%=filing.getNoindiv100kcnt()%><br/>
        Number of contractors greater than $100K: <%=filing.getNocontractor100kcnt()%><br/>
        Total contributions: <%=filing.getTotcntrbgfts()%><br/>
        Program service revenue code 2a: <%=filing.getPrgmservcode2acd()%><br/>
        Program service revenue amount 2a: <%=filing.getTotrev2acola()%><br/>
        Program service revenue code 2b: <%=filing.getPrgmservcode2bcd()%><br/>
        Program service revenue amount 2b: <%=filing.getTotrev2bcola()%><br/>
        Program service revenue code 2c: <%=filing.getPrgmservcode2ccd()%><br/>
        Program service revenue amount 2c: <%=filing.getTotrev2ccola()%><br/>
        Program service revenue code 2d: <%=filing.getPrgmservcode2dcd()%><br/>
        Program service revenue amount 2d: <%=filing.getTotrev2dcola()%><br/>
        Program service revenue code 2e: <%=filing.getPrgmservcode2ecd()%><br/>
        Program service revenue amount 2e: <%=filing.getTotrev2ecola()%><br/>
        Program service revenue amount 2f: <%=filing.getTotrev2fcola()%><br/>
        Program service revenue: <%=filing.getTotprgmrevnue()%><br/>
        Investment income: <%=filing.getInvstmntinc()%><br/>
        Tax-exempt bond proceeds: <%=filing.getTxexmptbndsproceeds()%><br/>
        Royalties: <%=filing.getRoyaltsinc()%><br/>
        Gross rents -- Real estate: <%=filing.getGrsrntsreal()%><br/>
        Gross rents -- Personal property: <%=filing.getGrsrntsprsnl()%><br/>
        Rental expense -- Real estate: <%=filing.getRntlexpnsreal()%><br/>
        Rental expense -- Personal property: <%=filing.getRntlexpnsprsnl()%><br/>
        Net rent -- Real estate: <%=filing.getRntlincreal()%><br/>
        Net rent -- Personal property: <%=filing.getRntlincprsnl()%><br/>
        Net rental income: <%=filing.getNetrntlinc()%><br/>
        Gross sales -- Securities: <%=filing.getGrsalesecur()%><br/>
        Gross sales -- Other assets: <%=filing.getGrsalesothr()%><br/>
        Sales expense -- Securities: <%=filing.getCstbasisecur()%><br/>
        Sales expense -- Other assets: <%=filing.getCstbasisothr()%><br/>
        Net gain from sales -- Securities: <%=filing.getGnlsecur()%><br/>
        Net gain from sales -- Other assets: <%=filing.getGnlsothr()%><br/>
        Sales of assets: <%=filing.getNetgnls()%><br/>
        Gross fundraising: <%=filing.getGrsincfndrsng()%><br/>
        Fundraising expenses: <%=filing.getLessdirfndrsng()%><br/>
        Fundraising income: <%=filing.getNetincfndrsng()%><br/>
        Gross income from gaming: <%=filing.getGrsincgaming()%><br/>
        Gaming expenses: <%=filing.getLessdirgaming()%><br/>
        Gaming income: <%=filing.getNetincgaming()%><br/>
        Gross sales of inventory: <%=filing.getGrsalesinvent()%><br/>
        Cost of goods sold (inventory): <%=filing.getLesscstofgoods()%><br/>
        Income from sales of inventory: <%=filing.getNetincsales()%><br/>
        Other revenue code 11a: <%=filing.getMiscrev11acd()%><br/>
        Other revenue amount 11a: <%=filing.getMiscrevtota()%><br/>
        Other revenue code 11b: <%=filing.getMiscrev11bcd()%><br/>
        Other revenue amount 11b: <%=filing.getMiscrevtot11b()%><br/>
        Other revenue code 11c: <%=filing.getMiscrev11ccd()%><br/>
        Other revenue amount 11c: <%=filing.getMiscrevtot11c()%><br/>
        Other revenue amount 11d: <%=filing.getMiscrevtot11d()%><br/>
        Other revenue: <%=filing.getMiscrevtot11e()%><br/>
        Total revenue: <%=filing.getTotrevenue()%><br/>
        Grants to governments/orgs in the US: <%=filing.getGrntstogovt()%><br/>
        Grants to individuals in the US: <%=filing.getGrnsttoindiv()%><br/>
        Grants to orgs and individuals outside the US: <%=filing.getGrntstofrgngovt()%><br/>
        Benefits paid to or for members: <%=filing.getBenifitsmembrs()%><br/>
        Compensation of current officers, directors, etc : <%=filing.getCompnsatncurrofcr()%><br/>
        Compensation of disqualified persons: <%=filing.getCompnsatnandothr()%><br/>
        Other salaries and wages: <%=filing.getOthrsalwages()%><br/>
        Pension plan contributions: <%=filing.getPensionplancontrb()%><br/>
        Other employee benefits: <%=filing.getOthremplyeebenef()%><br/>
        Payroll taxes: <%=filing.getPayrolltx()%><br/>
        Management fees: <%=filing.getFeesforsrvcmgmt()%><br/>
        Legal fees: <%=filing.getLegalfees()%><br/>
        Accounting fees: <%=filing.getAccntingfees()%><br/>
        Lobbying fees: <%=filing.getFeesforsrvclobby()%><br/>
        Professional fundraising fees: <%=filing.getProfndraising()%><br/>
        Investment management feed: <%=filing.getFeesforsrvcinvstmgmt()%><br/>
        Other fees: <%=filing.getFeesforsrvcothr()%><br/>
        Advertising and promotion: <%=filing.getAdvrtpromo()%><br/>
        Office expenses: <%=filing.getOfficexpns()%><br/>
        Information technology: <%=filing.getInfotech()%><br/>
        Royalties: <%=filing.getRoyaltsexpns()%><br/>
        Occupancy: <%=filing.getOccupancy()%><br/>
        Travel: <%=filing.getTravel()%><br/>
        Travel/entertainment expenses to public officials: <%=filing.getTravelofpublicoffcl()%><br/>
        Conferences, conventions, meetings: <%=filing.getConverconventmtng()%><br/>
        Interest expense: <%=filing.getInterestamt()%><br/>
        Payments to affiliates: <%=filing.getPymtoaffiliates()%><br/>
        Depreciation, depletion, amortization: <%=filing.getDeprcatndepletn()%><br/>
        Insurance: <%=filing.getInsurance()%><br/>
        Other expenses 24a: <%=filing.getOthrexpnsa()%><br/>
        Other expenses 24b: <%=filing.getOthrexpnsb()%><br/>
        Other expenses 24c: <%=filing.getOthrexpnsc()%><br/>
        Other expenses 24d: <%=filing.getOthrexpnsd()%><br/>
        Other expenses 24e: <%=filing.getOthrexpnse()%><br/>
        Other expenses 24f: <%=filing.getOthrexpnsf()%><br/>
        Total functional expenses: <%=filing.getTotfuncexpns()%><br/>
        Cash -- non-interest bearing -- eoy: <%=filing.getNonintcashend()%><br/>
        Savings and temporary cash investments -- eoy: <%=filing.getSvngstempinvend()%><br/>
        Pledges and grants receivable -- eoy: <%=filing.getPldgegrntrcvblend()%><br/>
        Accounts receivable -- eoy: <%=filing.getAccntsrcvblend()%><br/>
        Receivables from officers, directors, etc. -- eoy: <%=filing.getCurrfrmrcvblend()%><br/>
        Receivables from disqualified persons -- eoy: <%=filing.getRcvbldisqualend()%><br/>
        Notes and loans receivables -- eoy: <%=filing.getNotesloansrcvblend()%><br/>
        Inventories for sale or use -- eoy: <%=filing.getInvntriesalesend()%><br/>
        Prepaid expenses or deferred charges -- eoy: <%=filing.getPrepaidexpnsend()%><br/>
        Land, buildings, & equipment (net) -- eoy: <%=filing.getLndbldgsequipend()%><br/>
        Investments in publicly traded securities -- eoy: <%=filing.getInvstmntsend()%><br/>
        Investments in other securities -- eoy: <%=filing.getInvstmntsothrend()%><br/>
        Program-related investments -- eoy: <%=filing.getInvstmntsprgmend()%><br/>
        Intangible assets -- eoy: <%=filing.getIntangibleassetsend()%><br/>
        Other assets -- eoy: <%=filing.getOthrassetsend()%><br/>
        Total assets -- eoy: <%=filing.getTotassetsend()%><br/>
        Accounts payable and accrued expenses -- eoy: <%=filing.getAccntspayableend()%><br/>
        Grants payable -- eoy: <%=filing.getGrntspayableend()%><br/>
        Deferred revenue -- eoy: <%=filing.getDeferedrevnuend()%><br/>
        Tax-exempt bond liabilities -- eoy: <%=filing.getTxexmptbndsend()%><br/>
        Escrow account liability -- eoy: <%=filing.getEscrwaccntliabend()%><br/>
        Payables to officers, directors, etc. -- eoy: <%=filing.getPaybletoffcrsend()%><br/>
        Secured mortgages and notes payable -- eoy: <%=filing.getSecrdmrtgsend()%><br/>
        Unsecured mortgages and notes payable -- eoy: <%=filing.getUnsecurednotesend()%><br/>
        Other liabilities -- eoy: <%=filing.getOthrliabend()%><br/>
        Total liabilities -- eoy: <%=filing.getTotliabend()%><br/>
        Unrestricted net assets -- eoy: <%=filing.getUnrstrctnetasstsend()%><br/>
        Temporarily restricted net assets -- eoy: <%=filing.getTemprstrctnetasstsend()%><br/>
        Permanently restricted net assets -- eoy: <%=filing.getPermrstrctnetasstsend()%><br/>
        Capital stock or trust principal -- eoy: <%=filing.getCapitalstktrstend()%><br/>
        Paid-in or capital surplus -- eoy: <%=filing.getPaidinsurplusend()%><br/>
        Retained earnings -- eoy: <%=filing.getRetainedearnend()%><br/>
        Total Net Assets -- eoy: <%=filing.getTotnetassetend()%><br/>
        Total Liabilities + Net Assets -- eoy: <%=filing.getTotnetliabastend()%><br/>
        Reason for non-PF status: <%=filing.getNonpfrea()%><br/>
        Number of organizations supported: <%=filing.getTotnooforgscnt()%><br/>
        Sum of amounts of support: <%=filing.getTotsupport()%><br/>
        Gifts grants membership fees received (170): <%=filing.getGftgrntsrcvd170()%><br/>
        Tax revenues levied (170): <%=filing.getTxrevnuelevied170()%><br/>
        Services or facilities furnished by gov (170): <%=filing.getSrvcsval170()%><br/>
        Public support subtotal (170): <%=filing.getPubsuppsubtot170()%><br/>
        Amount support exceeds total (170): <%=filing.getExceeds2pct170()%><br/>
        Public support (170): <%=filing.getPubsupplesspct170()%><br/>
        Public support from line 4 (170): <%=filing.getSamepubsuppsubtot170()%><br/>
        Gross income from interest etc (170): <%=filing.getGrsinc170()%><br/>
        Net UBI (170): <%=filing.getNetincunreltd170()%><br/>
        Other income (170): <%=filing.getOthrinc170()%><br/>
        Total support (170): <%=filing.getTotsupp170()%><br/>
        Gross receipts from related activities (170): <%=filing.getGrsrcptsrelated170()%><br/>
        Gifts grants membership fees received (509): <%=filing.getTotgftgrntrcvd509()%><br/>
        Receipts from admissions merchandise etc (509): <%=filing.getGrsrcptsadmissn509()%><br/>
        Gross receipts from related activities (509): <%=filing.getGrsrcptsactivities509()%><br/>
        Tax revenues levied (509): <%=filing.getTxrevnuelevied509()%><br/>
        Services or facilities furnished by gov (509): <%=filing.getSrvcsval509()%><br/>
        Public support subtotal (509): <%=filing.getPubsuppsubtot509()%><br/>
        Amounts from disqualified persons (509): <%=filing.getRcvdfrmdisqualsub509()%><br/>
        Amount support exceeds total (509): <%=filing.getExceeds1pct509()%><br/>
        Public support subtotal (509): <%=filing.getSubtotpub509()%><br/>
        Public support (509): <%=filing.getPubsupplesub509()%><br/>
        Public support from line 6 (509): <%=filing.getSamepubsuppsubtot509()%><br/>
        Gross income from interest etc (509): <%=filing.getGrsinc509()%><br/>
        Net UBI (509): <%=filing.getUnreltxincls511tx509()%><br/>
        Subtotal total support (509): <%=filing.getSubtotsuppinc509()%><br/>
        Net income from UBI not in 10b (509): <%=filing.getNetincunrelatd509()%><br/>
        Other income (509): <%=filing.getOthrinc509()%><br/>
        Total support (509): <%=filing.getTotsupp509()%><br/>

    </div>
</div>
<%}%>
<a href="https://ackdev.com" target="_blank">Free Non for Profit Lookup Service by AckDev Inc.</a>
</body>
</html>
