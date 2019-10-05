package com.ackdev.nfp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="filing")
public class Filing {

    private String elf;
    @Id
    private String EIN;
    private String tax_pd;
    private String subseccd;
    private String s501c3or4947a1cd;
    private String schdbind;
    private String politicalactvtscd;
    private String lbbyingactvtscd;
    private String subjto6033cd;
    private String dnradvisedfundscd;
    private String prptyintrcvdcd;
    private String maintwrkofartcd;
    private String crcounselingqstncd;
    private String hldassetsintermpermcd;
    private String rptlndbldgeqptcd;
    private String rptinvstothsecd;
    private String rptinvstprgrelcd;
    private String rptothasstcd;
    private String rptothliabcd;
    private String sepcnsldtfinstmtcd;
    private String sepindaudfinstmtcd;
    private String inclinfinstmtcd;
    private String operateschools170cd;
    private String frgnofficecd;
    private String frgnrevexpnscd;
    private String frgngrntscd;
    private String frgnaggragrntscd;
    private String rptprofndrsngfeescd;
    private String rptincfnndrsngcd;
    private String rptincgamingcd;
    private String operatehosptlcd;
    private String hospaudfinstmtcd;
    private String rptgrntstogovtcd;
    private String rptgrntstoindvcd;
    private String rptyestocompnstncd;
    private String txexmptbndcd;
    private String invstproceedscd;
    private String maintescrwaccntcd;
    private String actonbehalfcd;
    private String engageexcessbnftcd;
    private String awarexcessbnftcd;
    private String loantofficercd;
    private String grantoofficercd;
    private String dirbusnreltdcd;
    private String fmlybusnreltdcd;
    private String servasofficercd;
    private String recvnoncashcd;
    private String recvartcd;
    private String ceaseoperationscd;
    private String sellorexchcd;
    private String ownsepentcd;
    private String reltdorgcd;
    private String intincntrlcd;
    private String orgtrnsfrcd;
    private String conduct5percentcd;
    private String compltschocd;
    private String f1096cnt;
    private String fw2gcnt;
    private String wthldngrulescd;
    private String noemplyeesw3cnt;
    private String filerqrdrtnscd;
    private String unrelbusinccd;
    private String filedf990tcd;
    private String frgnacctcd;
    private String prohibtdtxshltrcd;
    private String prtynotifyorgcd;
    private String filedf8886tcd;
    private String solicitcntrbcd;
    private String exprstmntcd;
    private String providegoodscd;
    private String notfydnrvalcd;
    private String filedf8282cd;
    private String f8282cnt;
    private String fndsrcvdcd;
    private String premiumspaidcd;
    private String filedf8899cd;
    private String filedf1098ccd;
    private String excbushldngscd;
    private String s4966distribcd;
    private String distribtodonorcd;
    private String initiationfees;
    private String grsrcptspublicuse;
    private String grsincmembers;
    private String grsincother;
    private String filedlieuf1041cd;
    private String txexmptint;
    private String qualhlthplncd;
    private String qualhlthreqmntn;
    private String qualhlthonhnd;
    private String rcvdpdtngcd;
    private String filedf720cd;
    private String totreprtabled;
    private String totcomprelatede;
    private String totestcompf;
    private String noindiv100kcnt;
    private String nocontractor100kcnt;
    private String totcntrbgfts;
    private String prgmservcode2acd;
    private String totrev2acola;
    private String prgmservcode2bcd;
    private String totrev2bcola;
    private String prgmservcode2ccd;
    private String totrev2ccola;
    private String prgmservcode2dcd;
    private String totrev2dcola;
    private String prgmservcode2ecd;
    private String totrev2ecola;
    private String totrev2fcola;
    private String totprgmrevnue;
    private String invstmntinc;
    private String txexmptbndsproceeds;
    private String royaltsinc;
    private String grsrntsreal;
    private String grsrntsprsnl;
    private String rntlexpnsreal;
    private String rntlexpnsprsnl;
    private String rntlincreal;
    private String rntlincprsnl;
    private String netrntlinc;
    private String grsalesecur;
    private String grsalesothr;
    private String cstbasisecur;
    private String cstbasisothr;
    private String gnlsecur;
    private String gnlsothr;
    private String netgnls;
    private String grsincfndrsng;
    private String lessdirfndrsng;
    private String netincfndrsng;
    private String grsincgaming;
    private String lessdirgaming;
    private String netincgaming;
    private String grsalesinvent;
    private String lesscstofgoods;
    private String netincsales;
    private String miscrev11acd;
    private String miscrevtota;
    private String miscrev11bcd;
    private String miscrevtot11b;
    private String miscrev11ccd;
    private String miscrevtot11c;
    private String miscrevtot11d;
    private String miscrevtot11e;
    private String totrevenue;
    private String grntstogovt;
    private String grnsttoindiv;
    private String grntstofrgngovt;
    private String benifitsmembrs;
    private String compnsatncurrofcr;
    private String compnsatnandothr;
    private String othrsalwages;
    private String pensionplancontrb;
    private String othremplyeebenef;
    private String payrolltx;
    private String feesforsrvcmgmt;
    private String legalfees;
    private String accntingfees;
    private String feesforsrvclobby;
    private String profndraising;
    private String feesforsrvcinvstmgmt;
    private String feesforsrvcothr;
    private String advrtpromo;
    private String officexpns;
    private String infotech;
    private String royaltsexpns;
    private String occupancy;
    private String travel;
    private String travelofpublicoffcl;
    private String converconventmtng;
    private String interestamt;
    private String pymtoaffiliates;
    private String deprcatndepletn;
    private String insurance;
    private String othrexpnsa;
    private String othrexpnsb;
    private String othrexpnsc;
    private String othrexpnsd;
    private String othrexpnse;
    private String othrexpnsf;
    private String totfuncexpns;
    private String nonintcashend;
    private String svngstempinvend;
    private String pldgegrntrcvblend;
    private String accntsrcvblend;
    private String currfrmrcvblend;
    private String rcvbldisqualend;
    private String notesloansrcvblend;
    private String invntriesalesend;
    private String prepaidexpnsend;
    private String lndbldgsequipend;
    private String invstmntsend;
    private String invstmntsothrend;
    private String invstmntsprgmend;
    private String intangibleassetsend;
    private String othrassetsend;
    private String totassetsend;
    private String accntspayableend;
    private String grntspayableend;
    private String deferedrevnuend;
    private String txexmptbndsend;
    private String escrwaccntliabend;
    private String paybletoffcrsend;
    private String secrdmrtgsend;
    private String unsecurednotesend;
    private String othrliabend;
    private String totliabend;
    private String unrstrctnetasstsend;
    private String temprstrctnetasstsend;
    private String permrstrctnetasstsend;
    private String capitalstktrstend;
    private String paidinsurplusend;
    private String retainedearnend;
    private String totnetassetend;
    private String totnetliabastend;
    private String nonpfrea;
    private String totnooforgscnt;
    private String totsupport;
    private String gftgrntsrcvd170;
    private String txrevnuelevied170;
    private String srvcsval170;
    private String pubsuppsubtot170;
    private String exceeds2pct170;
    private String pubsupplesspct170;
    private String samepubsuppsubtot170;
    private String grsinc170;
    private String netincunreltd170;
    private String othrinc170;
    private String totsupp170;
    private String grsrcptsrelated170;
    private String totgftgrntrcvd509;
    private String grsrcptsadmissn509;
    private String grsrcptsactivities509;
    private String txrevnuelevied509;
    private String srvcsval509;
    private String pubsuppsubtot509;
    private String rcvdfrmdisqualsub509;
    private String exceeds1pct509;
    private String subtotpub509;
    private String pubsupplesub509;
    private String samepubsuppsubtot509;
    private String grsinc509;
    private String unreltxincls511tx509;
    private String subtotsuppinc509;
    private String netincunrelatd509;
    private String othrinc509;
    private String totsupp509;

    public String getElf() {
        return elf;
    }

    public void setElf(String elf) {
        this.elf = elf;
    }

    public String getEIN() {
        return EIN;
    }

    public void setEIN(String EIN) {
        this.EIN = EIN;
    }

    public String getTax_pd() {
        return tax_pd;
    }

    public void setTax_pd(String tax_pd) {
        this.tax_pd = tax_pd;
    }

    public String getSubseccd() {
        return subseccd;
    }

    public void setSubseccd(String subseccd) {
        this.subseccd = subseccd;
    }

    public String getS501c3or4947a1cd() {
        return s501c3or4947a1cd;
    }

    public void setS501c3or4947a1cd(String s501c3or4947a1cd) {
        this.s501c3or4947a1cd = s501c3or4947a1cd;
    }

    public String getSchdbind() {
        return schdbind;
    }

    public void setSchdbind(String schdbind) {
        this.schdbind = schdbind;
    }

    public String getPoliticalactvtscd() {
        return politicalactvtscd;
    }

    public void setPoliticalactvtscd(String politicalactvtscd) {
        this.politicalactvtscd = politicalactvtscd;
    }

    public String getLbbyingactvtscd() {
        return lbbyingactvtscd;
    }

    public void setLbbyingactvtscd(String lbbyingactvtscd) {
        this.lbbyingactvtscd = lbbyingactvtscd;
    }

    public String getSubjto6033cd() {
        return subjto6033cd;
    }

    public void setSubjto6033cd(String subjto6033cd) {
        this.subjto6033cd = subjto6033cd;
    }

    public String getDnradvisedfundscd() {
        return dnradvisedfundscd;
    }

    public void setDnradvisedfundscd(String dnradvisedfundscd) {
        this.dnradvisedfundscd = dnradvisedfundscd;
    }

    public String getPrptyintrcvdcd() {
        return prptyintrcvdcd;
    }

    public void setPrptyintrcvdcd(String prptyintrcvdcd) {
        this.prptyintrcvdcd = prptyintrcvdcd;
    }

    public String getMaintwrkofartcd() {
        return maintwrkofartcd;
    }

    public void setMaintwrkofartcd(String maintwrkofartcd) {
        this.maintwrkofartcd = maintwrkofartcd;
    }

    public String getCrcounselingqstncd() {
        return crcounselingqstncd;
    }

    public void setCrcounselingqstncd(String crcounselingqstncd) {
        this.crcounselingqstncd = crcounselingqstncd;
    }

    public String getHldassetsintermpermcd() {
        return hldassetsintermpermcd;
    }

    public void setHldassetsintermpermcd(String hldassetsintermpermcd) {
        this.hldassetsintermpermcd = hldassetsintermpermcd;
    }

    public String getRptlndbldgeqptcd() {
        return rptlndbldgeqptcd;
    }

    public void setRptlndbldgeqptcd(String rptlndbldgeqptcd) {
        this.rptlndbldgeqptcd = rptlndbldgeqptcd;
    }

    public String getRptinvstothsecd() {
        return rptinvstothsecd;
    }

    public void setRptinvstothsecd(String rptinvstothsecd) {
        this.rptinvstothsecd = rptinvstothsecd;
    }

    public String getRptinvstprgrelcd() {
        return rptinvstprgrelcd;
    }

    public void setRptinvstprgrelcd(String rptinvstprgrelcd) {
        this.rptinvstprgrelcd = rptinvstprgrelcd;
    }

    public String getRptothasstcd() {
        return rptothasstcd;
    }

    public void setRptothasstcd(String rptothasstcd) {
        this.rptothasstcd = rptothasstcd;
    }

    public String getRptothliabcd() {
        return rptothliabcd;
    }

    public void setRptothliabcd(String rptothliabcd) {
        this.rptothliabcd = rptothliabcd;
    }

    public String getSepcnsldtfinstmtcd() {
        return sepcnsldtfinstmtcd;
    }

    public void setSepcnsldtfinstmtcd(String sepcnsldtfinstmtcd) {
        this.sepcnsldtfinstmtcd = sepcnsldtfinstmtcd;
    }

    public String getSepindaudfinstmtcd() {
        return sepindaudfinstmtcd;
    }

    public void setSepindaudfinstmtcd(String sepindaudfinstmtcd) {
        this.sepindaudfinstmtcd = sepindaudfinstmtcd;
    }

    public String getInclinfinstmtcd() {
        return inclinfinstmtcd;
    }

    public void setInclinfinstmtcd(String inclinfinstmtcd) {
        this.inclinfinstmtcd = inclinfinstmtcd;
    }

    public String getOperateschools170cd() {
        return operateschools170cd;
    }

    public void setOperateschools170cd(String operateschools170cd) {
        this.operateschools170cd = operateschools170cd;
    }

    public String getFrgnofficecd() {
        return frgnofficecd;
    }

    public void setFrgnofficecd(String frgnofficecd) {
        this.frgnofficecd = frgnofficecd;
    }

    public String getFrgnrevexpnscd() {
        return frgnrevexpnscd;
    }

    public void setFrgnrevexpnscd(String frgnrevexpnscd) {
        this.frgnrevexpnscd = frgnrevexpnscd;
    }

    public String getFrgngrntscd() {
        return frgngrntscd;
    }

    public void setFrgngrntscd(String frgngrntscd) {
        this.frgngrntscd = frgngrntscd;
    }

    public String getFrgnaggragrntscd() {
        return frgnaggragrntscd;
    }

    public void setFrgnaggragrntscd(String frgnaggragrntscd) {
        this.frgnaggragrntscd = frgnaggragrntscd;
    }

    public String getRptprofndrsngfeescd() {
        return rptprofndrsngfeescd;
    }

    public void setRptprofndrsngfeescd(String rptprofndrsngfeescd) {
        this.rptprofndrsngfeescd = rptprofndrsngfeescd;
    }

    public String getRptincfnndrsngcd() {
        return rptincfnndrsngcd;
    }

    public void setRptincfnndrsngcd(String rptincfnndrsngcd) {
        this.rptincfnndrsngcd = rptincfnndrsngcd;
    }

    public String getRptincgamingcd() {
        return rptincgamingcd;
    }

    public void setRptincgamingcd(String rptincgamingcd) {
        this.rptincgamingcd = rptincgamingcd;
    }

    public String getOperatehosptlcd() {
        return operatehosptlcd;
    }

    public void setOperatehosptlcd(String operatehosptlcd) {
        this.operatehosptlcd = operatehosptlcd;
    }

    public String getHospaudfinstmtcd() {
        return hospaudfinstmtcd;
    }

    public void setHospaudfinstmtcd(String hospaudfinstmtcd) {
        this.hospaudfinstmtcd = hospaudfinstmtcd;
    }

    public String getRptgrntstogovtcd() {
        return rptgrntstogovtcd;
    }

    public void setRptgrntstogovtcd(String rptgrntstogovtcd) {
        this.rptgrntstogovtcd = rptgrntstogovtcd;
    }

    public String getRptgrntstoindvcd() {
        return rptgrntstoindvcd;
    }

    public void setRptgrntstoindvcd(String rptgrntstoindvcd) {
        this.rptgrntstoindvcd = rptgrntstoindvcd;
    }

    public String getRptyestocompnstncd() {
        return rptyestocompnstncd;
    }

    public void setRptyestocompnstncd(String rptyestocompnstncd) {
        this.rptyestocompnstncd = rptyestocompnstncd;
    }

    public String getTxexmptbndcd() {
        return txexmptbndcd;
    }

    public void setTxexmptbndcd(String txexmptbndcd) {
        this.txexmptbndcd = txexmptbndcd;
    }

    public String getInvstproceedscd() {
        return invstproceedscd;
    }

    public void setInvstproceedscd(String invstproceedscd) {
        this.invstproceedscd = invstproceedscd;
    }

    public String getMaintescrwaccntcd() {
        return maintescrwaccntcd;
    }

    public void setMaintescrwaccntcd(String maintescrwaccntcd) {
        this.maintescrwaccntcd = maintescrwaccntcd;
    }

    public String getActonbehalfcd() {
        return actonbehalfcd;
    }

    public void setActonbehalfcd(String actonbehalfcd) {
        this.actonbehalfcd = actonbehalfcd;
    }

    public String getEngageexcessbnftcd() {
        return engageexcessbnftcd;
    }

    public void setEngageexcessbnftcd(String engageexcessbnftcd) {
        this.engageexcessbnftcd = engageexcessbnftcd;
    }

    public String getAwarexcessbnftcd() {
        return awarexcessbnftcd;
    }

    public void setAwarexcessbnftcd(String awarexcessbnftcd) {
        this.awarexcessbnftcd = awarexcessbnftcd;
    }

    public String getLoantofficercd() {
        return loantofficercd;
    }

    public void setLoantofficercd(String loantofficercd) {
        this.loantofficercd = loantofficercd;
    }

    public String getGrantoofficercd() {
        return grantoofficercd;
    }

    public void setGrantoofficercd(String grantoofficercd) {
        this.grantoofficercd = grantoofficercd;
    }

    public String getDirbusnreltdcd() {
        return dirbusnreltdcd;
    }

    public void setDirbusnreltdcd(String dirbusnreltdcd) {
        this.dirbusnreltdcd = dirbusnreltdcd;
    }

    public String getFmlybusnreltdcd() {
        return fmlybusnreltdcd;
    }

    public void setFmlybusnreltdcd(String fmlybusnreltdcd) {
        this.fmlybusnreltdcd = fmlybusnreltdcd;
    }

    public String getServasofficercd() {
        return servasofficercd;
    }

    public void setServasofficercd(String servasofficercd) {
        this.servasofficercd = servasofficercd;
    }

    public String getRecvnoncashcd() {
        return recvnoncashcd;
    }

    public void setRecvnoncashcd(String recvnoncashcd) {
        this.recvnoncashcd = recvnoncashcd;
    }

    public String getRecvartcd() {
        return recvartcd;
    }

    public void setRecvartcd(String recvartcd) {
        this.recvartcd = recvartcd;
    }

    public String getCeaseoperationscd() {
        return ceaseoperationscd;
    }

    public void setCeaseoperationscd(String ceaseoperationscd) {
        this.ceaseoperationscd = ceaseoperationscd;
    }

    public String getSellorexchcd() {
        return sellorexchcd;
    }

    public void setSellorexchcd(String sellorexchcd) {
        this.sellorexchcd = sellorexchcd;
    }

    public String getOwnsepentcd() {
        return ownsepentcd;
    }

    public void setOwnsepentcd(String ownsepentcd) {
        this.ownsepentcd = ownsepentcd;
    }

    public String getReltdorgcd() {
        return reltdorgcd;
    }

    public void setReltdorgcd(String reltdorgcd) {
        this.reltdorgcd = reltdorgcd;
    }

    public String getIntincntrlcd() {
        return intincntrlcd;
    }

    public void setIntincntrlcd(String intincntrlcd) {
        this.intincntrlcd = intincntrlcd;
    }

    public String getOrgtrnsfrcd() {
        return orgtrnsfrcd;
    }

    public void setOrgtrnsfrcd(String orgtrnsfrcd) {
        this.orgtrnsfrcd = orgtrnsfrcd;
    }

    public String getConduct5percentcd() {
        return conduct5percentcd;
    }

    public void setConduct5percentcd(String conduct5percentcd) {
        this.conduct5percentcd = conduct5percentcd;
    }

    public String getCompltschocd() {
        return compltschocd;
    }

    public void setCompltschocd(String compltschocd) {
        this.compltschocd = compltschocd;
    }

    public String getF1096cnt() {
        return f1096cnt;
    }

    public void setF1096cnt(String f1096cnt) {
        this.f1096cnt = f1096cnt;
    }

    public String getFw2gcnt() {
        return fw2gcnt;
    }

    public void setFw2gcnt(String fw2gcnt) {
        this.fw2gcnt = fw2gcnt;
    }

    public String getWthldngrulescd() {
        return wthldngrulescd;
    }

    public void setWthldngrulescd(String wthldngrulescd) {
        this.wthldngrulescd = wthldngrulescd;
    }

    public String getNoemplyeesw3cnt() {
        return noemplyeesw3cnt;
    }

    public void setNoemplyeesw3cnt(String noemplyeesw3cnt) {
        this.noemplyeesw3cnt = noemplyeesw3cnt;
    }

    public String getFilerqrdrtnscd() {
        return filerqrdrtnscd;
    }

    public void setFilerqrdrtnscd(String filerqrdrtnscd) {
        this.filerqrdrtnscd = filerqrdrtnscd;
    }

    public String getUnrelbusinccd() {
        return unrelbusinccd;
    }

    public void setUnrelbusinccd(String unrelbusinccd) {
        this.unrelbusinccd = unrelbusinccd;
    }

    public String getFiledf990tcd() {
        return filedf990tcd;
    }

    public void setFiledf990tcd(String filedf990tcd) {
        this.filedf990tcd = filedf990tcd;
    }

    public String getFrgnacctcd() {
        return frgnacctcd;
    }

    public void setFrgnacctcd(String frgnacctcd) {
        this.frgnacctcd = frgnacctcd;
    }

    public String getProhibtdtxshltrcd() {
        return prohibtdtxshltrcd;
    }

    public void setProhibtdtxshltrcd(String prohibtdtxshltrcd) {
        this.prohibtdtxshltrcd = prohibtdtxshltrcd;
    }

    public String getPrtynotifyorgcd() {
        return prtynotifyorgcd;
    }

    public void setPrtynotifyorgcd(String prtynotifyorgcd) {
        this.prtynotifyorgcd = prtynotifyorgcd;
    }

    public String getFiledf8886tcd() {
        return filedf8886tcd;
    }

    public void setFiledf8886tcd(String filedf8886tcd) {
        this.filedf8886tcd = filedf8886tcd;
    }

    public String getSolicitcntrbcd() {
        return solicitcntrbcd;
    }

    public void setSolicitcntrbcd(String solicitcntrbcd) {
        this.solicitcntrbcd = solicitcntrbcd;
    }

    public String getExprstmntcd() {
        return exprstmntcd;
    }

    public void setExprstmntcd(String exprstmntcd) {
        this.exprstmntcd = exprstmntcd;
    }

    public String getProvidegoodscd() {
        return providegoodscd;
    }

    public void setProvidegoodscd(String providegoodscd) {
        this.providegoodscd = providegoodscd;
    }

    public String getNotfydnrvalcd() {
        return notfydnrvalcd;
    }

    public void setNotfydnrvalcd(String notfydnrvalcd) {
        this.notfydnrvalcd = notfydnrvalcd;
    }

    public String getFiledf8282cd() {
        return filedf8282cd;
    }

    public void setFiledf8282cd(String filedf8282cd) {
        this.filedf8282cd = filedf8282cd;
    }

    public String getF8282cnt() {
        return f8282cnt;
    }

    public void setF8282cnt(String f8282cnt) {
        this.f8282cnt = f8282cnt;
    }

    public String getFndsrcvdcd() {
        return fndsrcvdcd;
    }

    public void setFndsrcvdcd(String fndsrcvdcd) {
        this.fndsrcvdcd = fndsrcvdcd;
    }

    public String getPremiumspaidcd() {
        return premiumspaidcd;
    }

    public void setPremiumspaidcd(String premiumspaidcd) {
        this.premiumspaidcd = premiumspaidcd;
    }

    public String getFiledf8899cd() {
        return filedf8899cd;
    }

    public void setFiledf8899cd(String filedf8899cd) {
        this.filedf8899cd = filedf8899cd;
    }

    public String getFiledf1098ccd() {
        return filedf1098ccd;
    }

    public void setFiledf1098ccd(String filedf1098ccd) {
        this.filedf1098ccd = filedf1098ccd;
    }

    public String getExcbushldngscd() {
        return excbushldngscd;
    }

    public void setExcbushldngscd(String excbushldngscd) {
        this.excbushldngscd = excbushldngscd;
    }

    public String getS4966distribcd() {
        return s4966distribcd;
    }

    public void setS4966distribcd(String s4966distribcd) {
        this.s4966distribcd = s4966distribcd;
    }

    public String getDistribtodonorcd() {
        return distribtodonorcd;
    }

    public void setDistribtodonorcd(String distribtodonorcd) {
        this.distribtodonorcd = distribtodonorcd;
    }

    public String getInitiationfees() {
        return initiationfees;
    }

    public void setInitiationfees(String initiationfees) {
        this.initiationfees = initiationfees;
    }

    public String getGrsrcptspublicuse() {
        return grsrcptspublicuse;
    }

    public void setGrsrcptspublicuse(String grsrcptspublicuse) {
        this.grsrcptspublicuse = grsrcptspublicuse;
    }

    public String getGrsincmembers() {
        return grsincmembers;
    }

    public void setGrsincmembers(String grsincmembers) {
        this.grsincmembers = grsincmembers;
    }

    public String getGrsincother() {
        return grsincother;
    }

    public void setGrsincother(String grsincother) {
        this.grsincother = grsincother;
    }

    public String getFiledlieuf1041cd() {
        return filedlieuf1041cd;
    }

    public void setFiledlieuf1041cd(String filedlieuf1041cd) {
        this.filedlieuf1041cd = filedlieuf1041cd;
    }

    public String getTxexmptint() {
        return txexmptint;
    }

    public void setTxexmptint(String txexmptint) {
        this.txexmptint = txexmptint;
    }

    public String getQualhlthplncd() {
        return qualhlthplncd;
    }

    public void setQualhlthplncd(String qualhlthplncd) {
        this.qualhlthplncd = qualhlthplncd;
    }

    public String getQualhlthreqmntn() {
        return qualhlthreqmntn;
    }

    public void setQualhlthreqmntn(String qualhlthreqmntn) {
        this.qualhlthreqmntn = qualhlthreqmntn;
    }

    public String getQualhlthonhnd() {
        return qualhlthonhnd;
    }

    public void setQualhlthonhnd(String qualhlthonhnd) {
        this.qualhlthonhnd = qualhlthonhnd;
    }

    public String getRcvdpdtngcd() {
        return rcvdpdtngcd;
    }

    public void setRcvdpdtngcd(String rcvdpdtngcd) {
        this.rcvdpdtngcd = rcvdpdtngcd;
    }

    public String getFiledf720cd() {
        return filedf720cd;
    }

    public void setFiledf720cd(String filedf720cd) {
        this.filedf720cd = filedf720cd;
    }

    public String getTotreprtabled() {
        return totreprtabled;
    }

    public void setTotreprtabled(String totreprtabled) {
        this.totreprtabled = totreprtabled;
    }

    public String getTotcomprelatede() {
        return totcomprelatede;
    }

    public void setTotcomprelatede(String totcomprelatede) {
        this.totcomprelatede = totcomprelatede;
    }

    public String getTotestcompf() {
        return totestcompf;
    }

    public void setTotestcompf(String totestcompf) {
        this.totestcompf = totestcompf;
    }

    public String getNoindiv100kcnt() {
        return noindiv100kcnt;
    }

    public void setNoindiv100kcnt(String noindiv100kcnt) {
        this.noindiv100kcnt = noindiv100kcnt;
    }

    public String getNocontractor100kcnt() {
        return nocontractor100kcnt;
    }

    public void setNocontractor100kcnt(String nocontractor100kcnt) {
        this.nocontractor100kcnt = nocontractor100kcnt;
    }

    public String getTotcntrbgfts() {
        return totcntrbgfts;
    }

    public void setTotcntrbgfts(String totcntrbgfts) {
        this.totcntrbgfts = totcntrbgfts;
    }

    public String getPrgmservcode2acd() {
        return prgmservcode2acd;
    }

    public void setPrgmservcode2acd(String prgmservcode2acd) {
        this.prgmservcode2acd = prgmservcode2acd;
    }

    public String getTotrev2acola() {
        return totrev2acola;
    }

    public void setTotrev2acola(String totrev2acola) {
        this.totrev2acola = totrev2acola;
    }

    public String getPrgmservcode2bcd() {
        return prgmservcode2bcd;
    }

    public void setPrgmservcode2bcd(String prgmservcode2bcd) {
        this.prgmservcode2bcd = prgmservcode2bcd;
    }

    public String getTotrev2bcola() {
        return totrev2bcola;
    }

    public void setTotrev2bcola(String totrev2bcola) {
        this.totrev2bcola = totrev2bcola;
    }

    public String getPrgmservcode2ccd() {
        return prgmservcode2ccd;
    }

    public void setPrgmservcode2ccd(String prgmservcode2ccd) {
        this.prgmservcode2ccd = prgmservcode2ccd;
    }

    public String getTotrev2ccola() {
        return totrev2ccola;
    }

    public void setTotrev2ccola(String totrev2ccola) {
        this.totrev2ccola = totrev2ccola;
    }

    public String getPrgmservcode2dcd() {
        return prgmservcode2dcd;
    }

    public void setPrgmservcode2dcd(String prgmservcode2dcd) {
        this.prgmservcode2dcd = prgmservcode2dcd;
    }

    public String getTotrev2dcola() {
        return totrev2dcola;
    }

    public void setTotrev2dcola(String totrev2dcola) {
        this.totrev2dcola = totrev2dcola;
    }

    public String getPrgmservcode2ecd() {
        return prgmservcode2ecd;
    }

    public void setPrgmservcode2ecd(String prgmservcode2ecd) {
        this.prgmservcode2ecd = prgmservcode2ecd;
    }

    public String getTotrev2ecola() {
        return totrev2ecola;
    }

    public void setTotrev2ecola(String totrev2ecola) {
        this.totrev2ecola = totrev2ecola;
    }

    public String getTotrev2fcola() {
        return totrev2fcola;
    }

    public void setTotrev2fcola(String totrev2fcola) {
        this.totrev2fcola = totrev2fcola;
    }

    public String getTotprgmrevnue() {
        return totprgmrevnue;
    }

    public void setTotprgmrevnue(String totprgmrevnue) {
        this.totprgmrevnue = totprgmrevnue;
    }

    public String getInvstmntinc() {
        return invstmntinc;
    }

    public void setInvstmntinc(String invstmntinc) {
        this.invstmntinc = invstmntinc;
    }

    public String getTxexmptbndsproceeds() {
        return txexmptbndsproceeds;
    }

    public void setTxexmptbndsproceeds(String txexmptbndsproceeds) {
        this.txexmptbndsproceeds = txexmptbndsproceeds;
    }

    public String getRoyaltsinc() {
        return royaltsinc;
    }

    public void setRoyaltsinc(String royaltsinc) {
        this.royaltsinc = royaltsinc;
    }

    public String getGrsrntsreal() {
        return grsrntsreal;
    }

    public void setGrsrntsreal(String grsrntsreal) {
        this.grsrntsreal = grsrntsreal;
    }

    public String getGrsrntsprsnl() {
        return grsrntsprsnl;
    }

    public void setGrsrntsprsnl(String grsrntsprsnl) {
        this.grsrntsprsnl = grsrntsprsnl;
    }

    public String getRntlexpnsreal() {
        return rntlexpnsreal;
    }

    public void setRntlexpnsreal(String rntlexpnsreal) {
        this.rntlexpnsreal = rntlexpnsreal;
    }

    public String getRntlexpnsprsnl() {
        return rntlexpnsprsnl;
    }

    public void setRntlexpnsprsnl(String rntlexpnsprsnl) {
        this.rntlexpnsprsnl = rntlexpnsprsnl;
    }

    public String getRntlincreal() {
        return rntlincreal;
    }

    public void setRntlincreal(String rntlincreal) {
        this.rntlincreal = rntlincreal;
    }

    public String getRntlincprsnl() {
        return rntlincprsnl;
    }

    public void setRntlincprsnl(String rntlincprsnl) {
        this.rntlincprsnl = rntlincprsnl;
    }

    public String getNetrntlinc() {
        return netrntlinc;
    }

    public void setNetrntlinc(String netrntlinc) {
        this.netrntlinc = netrntlinc;
    }

    public String getGrsalesecur() {
        return grsalesecur;
    }

    public void setGrsalesecur(String grsalesecur) {
        this.grsalesecur = grsalesecur;
    }

    public String getGrsalesothr() {
        return grsalesothr;
    }

    public void setGrsalesothr(String grsalesothr) {
        this.grsalesothr = grsalesothr;
    }

    public String getCstbasisecur() {
        return cstbasisecur;
    }

    public void setCstbasisecur(String cstbasisecur) {
        this.cstbasisecur = cstbasisecur;
    }

    public String getCstbasisothr() {
        return cstbasisothr;
    }

    public void setCstbasisothr(String cstbasisothr) {
        this.cstbasisothr = cstbasisothr;
    }

    public String getGnlsecur() {
        return gnlsecur;
    }

    public void setGnlsecur(String gnlsecur) {
        this.gnlsecur = gnlsecur;
    }

    public String getGnlsothr() {
        return gnlsothr;
    }

    public void setGnlsothr(String gnlsothr) {
        this.gnlsothr = gnlsothr;
    }

    public String getNetgnls() {
        return netgnls;
    }

    public void setNetgnls(String netgnls) {
        this.netgnls = netgnls;
    }

    public String getGrsincfndrsng() {
        return grsincfndrsng;
    }

    public void setGrsincfndrsng(String grsincfndrsng) {
        this.grsincfndrsng = grsincfndrsng;
    }

    public String getLessdirfndrsng() {
        return lessdirfndrsng;
    }

    public void setLessdirfndrsng(String lessdirfndrsng) {
        this.lessdirfndrsng = lessdirfndrsng;
    }

    public String getNetincfndrsng() {
        return netincfndrsng;
    }

    public void setNetincfndrsng(String netincfndrsng) {
        this.netincfndrsng = netincfndrsng;
    }

    public String getGrsincgaming() {
        return grsincgaming;
    }

    public void setGrsincgaming(String grsincgaming) {
        this.grsincgaming = grsincgaming;
    }

    public String getLessdirgaming() {
        return lessdirgaming;
    }

    public void setLessdirgaming(String lessdirgaming) {
        this.lessdirgaming = lessdirgaming;
    }

    public String getNetincgaming() {
        return netincgaming;
    }

    public void setNetincgaming(String netincgaming) {
        this.netincgaming = netincgaming;
    }

    public String getGrsalesinvent() {
        return grsalesinvent;
    }

    public void setGrsalesinvent(String grsalesinvent) {
        this.grsalesinvent = grsalesinvent;
    }

    public String getLesscstofgoods() {
        return lesscstofgoods;
    }

    public void setLesscstofgoods(String lesscstofgoods) {
        this.lesscstofgoods = lesscstofgoods;
    }

    public String getNetincsales() {
        return netincsales;
    }

    public void setNetincsales(String netincsales) {
        this.netincsales = netincsales;
    }

    public String getMiscrev11acd() {
        return miscrev11acd;
    }

    public void setMiscrev11acd(String miscrev11acd) {
        this.miscrev11acd = miscrev11acd;
    }

    public String getMiscrevtota() {
        return miscrevtota;
    }

    public void setMiscrevtota(String miscrevtota) {
        this.miscrevtota = miscrevtota;
    }

    public String getMiscrev11bcd() {
        return miscrev11bcd;
    }

    public void setMiscrev11bcd(String miscrev11bcd) {
        this.miscrev11bcd = miscrev11bcd;
    }

    public String getMiscrevtot11b() {
        return miscrevtot11b;
    }

    public void setMiscrevtot11b(String miscrevtot11b) {
        this.miscrevtot11b = miscrevtot11b;
    }

    public String getMiscrev11ccd() {
        return miscrev11ccd;
    }

    public void setMiscrev11ccd(String miscrev11ccd) {
        this.miscrev11ccd = miscrev11ccd;
    }

    public String getMiscrevtot11c() {
        return miscrevtot11c;
    }

    public void setMiscrevtot11c(String miscrevtot11c) {
        this.miscrevtot11c = miscrevtot11c;
    }

    public String getMiscrevtot11d() {
        return miscrevtot11d;
    }

    public void setMiscrevtot11d(String miscrevtot11d) {
        this.miscrevtot11d = miscrevtot11d;
    }

    public String getMiscrevtot11e() {
        return miscrevtot11e;
    }

    public void setMiscrevtot11e(String miscrevtot11e) {
        this.miscrevtot11e = miscrevtot11e;
    }

    public String getTotrevenue() {
        return totrevenue;
    }

    public void setTotrevenue(String totrevenue) {
        this.totrevenue = totrevenue;
    }

    public String getGrntstogovt() {
        return grntstogovt;
    }

    public void setGrntstogovt(String grntstogovt) {
        this.grntstogovt = grntstogovt;
    }

    public String getGrnsttoindiv() {
        return grnsttoindiv;
    }

    public void setGrnsttoindiv(String grnsttoindiv) {
        this.grnsttoindiv = grnsttoindiv;
    }

    public String getGrntstofrgngovt() {
        return grntstofrgngovt;
    }

    public void setGrntstofrgngovt(String grntstofrgngovt) {
        this.grntstofrgngovt = grntstofrgngovt;
    }

    public String getBenifitsmembrs() {
        return benifitsmembrs;
    }

    public void setBenifitsmembrs(String benifitsmembrs) {
        this.benifitsmembrs = benifitsmembrs;
    }

    public String getCompnsatncurrofcr() {
        return compnsatncurrofcr;
    }

    public void setCompnsatncurrofcr(String compnsatncurrofcr) {
        this.compnsatncurrofcr = compnsatncurrofcr;
    }

    public String getCompnsatnandothr() {
        return compnsatnandothr;
    }

    public void setCompnsatnandothr(String compnsatnandothr) {
        this.compnsatnandothr = compnsatnandothr;
    }

    public String getOthrsalwages() {
        return othrsalwages;
    }

    public void setOthrsalwages(String othrsalwages) {
        this.othrsalwages = othrsalwages;
    }

    public String getPensionplancontrb() {
        return pensionplancontrb;
    }

    public void setPensionplancontrb(String pensionplancontrb) {
        this.pensionplancontrb = pensionplancontrb;
    }

    public String getOthremplyeebenef() {
        return othremplyeebenef;
    }

    public void setOthremplyeebenef(String othremplyeebenef) {
        this.othremplyeebenef = othremplyeebenef;
    }

    public String getPayrolltx() {
        return payrolltx;
    }

    public void setPayrolltx(String payrolltx) {
        this.payrolltx = payrolltx;
    }

    public String getFeesforsrvcmgmt() {
        return feesforsrvcmgmt;
    }

    public void setFeesforsrvcmgmt(String feesforsrvcmgmt) {
        this.feesforsrvcmgmt = feesforsrvcmgmt;
    }

    public String getLegalfees() {
        return legalfees;
    }

    public void setLegalfees(String legalfees) {
        this.legalfees = legalfees;
    }

    public String getAccntingfees() {
        return accntingfees;
    }

    public void setAccntingfees(String accntingfees) {
        this.accntingfees = accntingfees;
    }

    public String getFeesforsrvclobby() {
        return feesforsrvclobby;
    }

    public void setFeesforsrvclobby(String feesforsrvclobby) {
        this.feesforsrvclobby = feesforsrvclobby;
    }

    public String getProfndraising() {
        return profndraising;
    }

    public void setProfndraising(String profndraising) {
        this.profndraising = profndraising;
    }

    public String getFeesforsrvcinvstmgmt() {
        return feesforsrvcinvstmgmt;
    }

    public void setFeesforsrvcinvstmgmt(String feesforsrvcinvstmgmt) {
        this.feesforsrvcinvstmgmt = feesforsrvcinvstmgmt;
    }

    public String getFeesforsrvcothr() {
        return feesforsrvcothr;
    }

    public void setFeesforsrvcothr(String feesforsrvcothr) {
        this.feesforsrvcothr = feesforsrvcothr;
    }

    public String getAdvrtpromo() {
        return advrtpromo;
    }

    public void setAdvrtpromo(String advrtpromo) {
        this.advrtpromo = advrtpromo;
    }

    public String getOfficexpns() {
        return officexpns;
    }

    public void setOfficexpns(String officexpns) {
        this.officexpns = officexpns;
    }

    public String getInfotech() {
        return infotech;
    }

    public void setInfotech(String infotech) {
        this.infotech = infotech;
    }

    public String getRoyaltsexpns() {
        return royaltsexpns;
    }

    public void setRoyaltsexpns(String royaltsexpns) {
        this.royaltsexpns = royaltsexpns;
    }

    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public String getTravel() {
        return travel;
    }

    public void setTravel(String travel) {
        this.travel = travel;
    }

    public String getTravelofpublicoffcl() {
        return travelofpublicoffcl;
    }

    public void setTravelofpublicoffcl(String travelofpublicoffcl) {
        this.travelofpublicoffcl = travelofpublicoffcl;
    }

    public String getConverconventmtng() {
        return converconventmtng;
    }

    public void setConverconventmtng(String converconventmtng) {
        this.converconventmtng = converconventmtng;
    }

    public String getInterestamt() {
        return interestamt;
    }

    public void setInterestamt(String interestamt) {
        this.interestamt = interestamt;
    }

    public String getPymtoaffiliates() {
        return pymtoaffiliates;
    }

    public void setPymtoaffiliates(String pymtoaffiliates) {
        this.pymtoaffiliates = pymtoaffiliates;
    }

    public String getDeprcatndepletn() {
        return deprcatndepletn;
    }

    public void setDeprcatndepletn(String deprcatndepletn) {
        this.deprcatndepletn = deprcatndepletn;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getOthrexpnsa() {
        return othrexpnsa;
    }

    public void setOthrexpnsa(String othrexpnsa) {
        this.othrexpnsa = othrexpnsa;
    }

    public String getOthrexpnsb() {
        return othrexpnsb;
    }

    public void setOthrexpnsb(String othrexpnsb) {
        this.othrexpnsb = othrexpnsb;
    }

    public String getOthrexpnsc() {
        return othrexpnsc;
    }

    public void setOthrexpnsc(String othrexpnsc) {
        this.othrexpnsc = othrexpnsc;
    }

    public String getOthrexpnsd() {
        return othrexpnsd;
    }

    public void setOthrexpnsd(String othrexpnsd) {
        this.othrexpnsd = othrexpnsd;
    }

    public String getOthrexpnse() {
        return othrexpnse;
    }

    public void setOthrexpnse(String othrexpnse) {
        this.othrexpnse = othrexpnse;
    }

    public String getOthrexpnsf() {
        return othrexpnsf;
    }

    public void setOthrexpnsf(String othrexpnsf) {
        this.othrexpnsf = othrexpnsf;
    }

    public String getTotfuncexpns() {
        return totfuncexpns;
    }

    public void setTotfuncexpns(String totfuncexpns) {
        this.totfuncexpns = totfuncexpns;
    }

    public String getNonintcashend() {
        return nonintcashend;
    }

    public void setNonintcashend(String nonintcashend) {
        this.nonintcashend = nonintcashend;
    }

    public String getSvngstempinvend() {
        return svngstempinvend;
    }

    public void setSvngstempinvend(String svngstempinvend) {
        this.svngstempinvend = svngstempinvend;
    }

    public String getPldgegrntrcvblend() {
        return pldgegrntrcvblend;
    }

    public void setPldgegrntrcvblend(String pldgegrntrcvblend) {
        this.pldgegrntrcvblend = pldgegrntrcvblend;
    }

    public String getAccntsrcvblend() {
        return accntsrcvblend;
    }

    public void setAccntsrcvblend(String accntsrcvblend) {
        this.accntsrcvblend = accntsrcvblend;
    }

    public String getCurrfrmrcvblend() {
        return currfrmrcvblend;
    }

    public void setCurrfrmrcvblend(String currfrmrcvblend) {
        this.currfrmrcvblend = currfrmrcvblend;
    }

    public String getRcvbldisqualend() {
        return rcvbldisqualend;
    }

    public void setRcvbldisqualend(String rcvbldisqualend) {
        this.rcvbldisqualend = rcvbldisqualend;
    }

    public String getNotesloansrcvblend() {
        return notesloansrcvblend;
    }

    public void setNotesloansrcvblend(String notesloansrcvblend) {
        this.notesloansrcvblend = notesloansrcvblend;
    }

    public String getInvntriesalesend() {
        return invntriesalesend;
    }

    public void setInvntriesalesend(String invntriesalesend) {
        this.invntriesalesend = invntriesalesend;
    }

    public String getPrepaidexpnsend() {
        return prepaidexpnsend;
    }

    public void setPrepaidexpnsend(String prepaidexpnsend) {
        this.prepaidexpnsend = prepaidexpnsend;
    }

    public String getLndbldgsequipend() {
        return lndbldgsequipend;
    }

    public void setLndbldgsequipend(String lndbldgsequipend) {
        this.lndbldgsequipend = lndbldgsequipend;
    }

    public String getInvstmntsend() {
        return invstmntsend;
    }

    public void setInvstmntsend(String invstmntsend) {
        this.invstmntsend = invstmntsend;
    }

    public String getInvstmntsothrend() {
        return invstmntsothrend;
    }

    public void setInvstmntsothrend(String invstmntsothrend) {
        this.invstmntsothrend = invstmntsothrend;
    }

    public String getInvstmntsprgmend() {
        return invstmntsprgmend;
    }

    public void setInvstmntsprgmend(String invstmntsprgmend) {
        this.invstmntsprgmend = invstmntsprgmend;
    }

    public String getIntangibleassetsend() {
        return intangibleassetsend;
    }

    public void setIntangibleassetsend(String intangibleassetsend) {
        this.intangibleassetsend = intangibleassetsend;
    }

    public String getOthrassetsend() {
        return othrassetsend;
    }

    public void setOthrassetsend(String othrassetsend) {
        this.othrassetsend = othrassetsend;
    }

    public String getTotassetsend() {
        return totassetsend;
    }

    public void setTotassetsend(String totassetsend) {
        this.totassetsend = totassetsend;
    }

    public String getAccntspayableend() {
        return accntspayableend;
    }

    public void setAccntspayableend(String accntspayableend) {
        this.accntspayableend = accntspayableend;
    }

    public String getGrntspayableend() {
        return grntspayableend;
    }

    public void setGrntspayableend(String grntspayableend) {
        this.grntspayableend = grntspayableend;
    }

    public String getDeferedrevnuend() {
        return deferedrevnuend;
    }

    public void setDeferedrevnuend(String deferedrevnuend) {
        this.deferedrevnuend = deferedrevnuend;
    }

    public String getTxexmptbndsend() {
        return txexmptbndsend;
    }

    public void setTxexmptbndsend(String txexmptbndsend) {
        this.txexmptbndsend = txexmptbndsend;
    }

    public String getEscrwaccntliabend() {
        return escrwaccntliabend;
    }

    public void setEscrwaccntliabend(String escrwaccntliabend) {
        this.escrwaccntliabend = escrwaccntliabend;
    }

    public String getPaybletoffcrsend() {
        return paybletoffcrsend;
    }

    public void setPaybletoffcrsend(String paybletoffcrsend) {
        this.paybletoffcrsend = paybletoffcrsend;
    }

    public String getSecrdmrtgsend() {
        return secrdmrtgsend;
    }

    public void setSecrdmrtgsend(String secrdmrtgsend) {
        this.secrdmrtgsend = secrdmrtgsend;
    }

    public String getUnsecurednotesend() {
        return unsecurednotesend;
    }

    public void setUnsecurednotesend(String unsecurednotesend) {
        this.unsecurednotesend = unsecurednotesend;
    }

    public String getOthrliabend() {
        return othrliabend;
    }

    public void setOthrliabend(String othrliabend) {
        this.othrliabend = othrliabend;
    }

    public String getTotliabend() {
        return totliabend;
    }

    public void setTotliabend(String totliabend) {
        this.totliabend = totliabend;
    }

    public String getUnrstrctnetasstsend() {
        return unrstrctnetasstsend;
    }

    public void setUnrstrctnetasstsend(String unrstrctnetasstsend) {
        this.unrstrctnetasstsend = unrstrctnetasstsend;
    }

    public String getTemprstrctnetasstsend() {
        return temprstrctnetasstsend;
    }

    public void setTemprstrctnetasstsend(String temprstrctnetasstsend) {
        this.temprstrctnetasstsend = temprstrctnetasstsend;
    }

    public String getPermrstrctnetasstsend() {
        return permrstrctnetasstsend;
    }

    public void setPermrstrctnetasstsend(String permrstrctnetasstsend) {
        this.permrstrctnetasstsend = permrstrctnetasstsend;
    }

    public String getCapitalstktrstend() {
        return capitalstktrstend;
    }

    public void setCapitalstktrstend(String capitalstktrstend) {
        this.capitalstktrstend = capitalstktrstend;
    }

    public String getPaidinsurplusend() {
        return paidinsurplusend;
    }

    public void setPaidinsurplusend(String paidinsurplusend) {
        this.paidinsurplusend = paidinsurplusend;
    }

    public String getRetainedearnend() {
        return retainedearnend;
    }

    public void setRetainedearnend(String retainedearnend) {
        this.retainedearnend = retainedearnend;
    }

    public String getTotnetassetend() {
        return totnetassetend;
    }

    public void setTotnetassetend(String totnetassetend) {
        this.totnetassetend = totnetassetend;
    }

    public String getTotnetliabastend() {
        return totnetliabastend;
    }

    public void setTotnetliabastend(String totnetliabastend) {
        this.totnetliabastend = totnetliabastend;
    }

    public String getNonpfrea() {
        return nonpfrea;
    }

    public void setNonpfrea(String nonpfrea) {
        this.nonpfrea = nonpfrea;
    }

    public String getTotnooforgscnt() {
        return totnooforgscnt;
    }

    public void setTotnooforgscnt(String totnooforgscnt) {
        this.totnooforgscnt = totnooforgscnt;
    }

    public String getTotsupport() {
        return totsupport;
    }

    public void setTotsupport(String totsupport) {
        this.totsupport = totsupport;
    }

    public String getGftgrntsrcvd170() {
        return gftgrntsrcvd170;
    }

    public void setGftgrntsrcvd170(String gftgrntsrcvd170) {
        this.gftgrntsrcvd170 = gftgrntsrcvd170;
    }

    public String getTxrevnuelevied170() {
        return txrevnuelevied170;
    }

    public void setTxrevnuelevied170(String txrevnuelevied170) {
        this.txrevnuelevied170 = txrevnuelevied170;
    }

    public String getSrvcsval170() {
        return srvcsval170;
    }

    public void setSrvcsval170(String srvcsval170) {
        this.srvcsval170 = srvcsval170;
    }

    public String getPubsuppsubtot170() {
        return pubsuppsubtot170;
    }

    public void setPubsuppsubtot170(String pubsuppsubtot170) {
        this.pubsuppsubtot170 = pubsuppsubtot170;
    }

    public String getExceeds2pct170() {
        return exceeds2pct170;
    }

    public void setExceeds2pct170(String exceeds2pct170) {
        this.exceeds2pct170 = exceeds2pct170;
    }

    public String getPubsupplesspct170() {
        return pubsupplesspct170;
    }

    public void setPubsupplesspct170(String pubsupplesspct170) {
        this.pubsupplesspct170 = pubsupplesspct170;
    }

    public String getSamepubsuppsubtot170() {
        return samepubsuppsubtot170;
    }

    public void setSamepubsuppsubtot170(String samepubsuppsubtot170) {
        this.samepubsuppsubtot170 = samepubsuppsubtot170;
    }

    public String getGrsinc170() {
        return grsinc170;
    }

    public void setGrsinc170(String grsinc170) {
        this.grsinc170 = grsinc170;
    }

    public String getNetincunreltd170() {
        return netincunreltd170;
    }

    public void setNetincunreltd170(String netincunreltd170) {
        this.netincunreltd170 = netincunreltd170;
    }

    public String getOthrinc170() {
        return othrinc170;
    }

    public void setOthrinc170(String othrinc170) {
        this.othrinc170 = othrinc170;
    }

    public String getTotsupp170() {
        return totsupp170;
    }

    public void setTotsupp170(String totsupp170) {
        this.totsupp170 = totsupp170;
    }

    public String getGrsrcptsrelated170() {
        return grsrcptsrelated170;
    }

    public void setGrsrcptsrelated170(String grsrcptsrelated170) {
        this.grsrcptsrelated170 = grsrcptsrelated170;
    }

    public String getTotgftgrntrcvd509() {
        return totgftgrntrcvd509;
    }

    public void setTotgftgrntrcvd509(String totgftgrntrcvd509) {
        this.totgftgrntrcvd509 = totgftgrntrcvd509;
    }

    public String getGrsrcptsadmissn509() {
        return grsrcptsadmissn509;
    }

    public void setGrsrcptsadmissn509(String grsrcptsadmissn509) {
        this.grsrcptsadmissn509 = grsrcptsadmissn509;
    }

    public String getGrsrcptsactivities509() {
        return grsrcptsactivities509;
    }

    public void setGrsrcptsactivities509(String grsrcptsactivities509) {
        this.grsrcptsactivities509 = grsrcptsactivities509;
    }

    public String getTxrevnuelevied509() {
        return txrevnuelevied509;
    }

    public void setTxrevnuelevied509(String txrevnuelevied509) {
        this.txrevnuelevied509 = txrevnuelevied509;
    }

    public String getSrvcsval509() {
        return srvcsval509;
    }

    public void setSrvcsval509(String srvcsval509) {
        this.srvcsval509 = srvcsval509;
    }

    public String getPubsuppsubtot509() {
        return pubsuppsubtot509;
    }

    public void setPubsuppsubtot509(String pubsuppsubtot509) {
        this.pubsuppsubtot509 = pubsuppsubtot509;
    }

    public String getRcvdfrmdisqualsub509() {
        return rcvdfrmdisqualsub509;
    }

    public void setRcvdfrmdisqualsub509(String rcvdfrmdisqualsub509) {
        this.rcvdfrmdisqualsub509 = rcvdfrmdisqualsub509;
    }

    public String getExceeds1pct509() {
        return exceeds1pct509;
    }

    public void setExceeds1pct509(String exceeds1pct509) {
        this.exceeds1pct509 = exceeds1pct509;
    }

    public String getSubtotpub509() {
        return subtotpub509;
    }

    public void setSubtotpub509(String subtotpub509) {
        this.subtotpub509 = subtotpub509;
    }

    public String getPubsupplesub509() {
        return pubsupplesub509;
    }

    public void setPubsupplesub509(String pubsupplesub509) {
        this.pubsupplesub509 = pubsupplesub509;
    }

    public String getSamepubsuppsubtot509() {
        return samepubsuppsubtot509;
    }

    public void setSamepubsuppsubtot509(String samepubsuppsubtot509) {
        this.samepubsuppsubtot509 = samepubsuppsubtot509;
    }

    public String getGrsinc509() {
        return grsinc509;
    }

    public void setGrsinc509(String grsinc509) {
        this.grsinc509 = grsinc509;
    }

    public String getUnreltxincls511tx509() {
        return unreltxincls511tx509;
    }

    public void setUnreltxincls511tx509(String unreltxincls511tx509) {
        this.unreltxincls511tx509 = unreltxincls511tx509;
    }

    public String getSubtotsuppinc509() {
        return subtotsuppinc509;
    }

    public void setSubtotsuppinc509(String subtotsuppinc509) {
        this.subtotsuppinc509 = subtotsuppinc509;
    }

    public String getNetincunrelatd509() {
        return netincunrelatd509;
    }

    public void setNetincunrelatd509(String netincunrelatd509) {
        this.netincunrelatd509 = netincunrelatd509;
    }

    public String getOthrinc509() {
        return othrinc509;
    }

    public void setOthrinc509(String othrinc509) {
        this.othrinc509 = othrinc509;
    }

    public String getTotsupp509() {
        return totsupp509;
    }

    public void setTotsupp509(String totsupp509) {
        this.totsupp509 = totsupp509;
    }
}
