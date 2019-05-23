package summit.baseproject.view.test.network;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

//대소문자 유의
@Root(name = "SiGunGuListResponse", strict = false)
public class NewAddressVO {

    public NewAddressVO(){}

    @Element(name = "cmmMsgHeader")
    public CmmMsgHeader cmmMsgHeader;


    @ElementList(entry="siGunGuList", inline = true)
    public List<SigunguList> sigunguList;

    //inline class로 쓸경후 public static 붙이기
    public static class SigunguList {
        public String getSignguCd() {
            return signguCd;
        }

        public void setSignguCd(String signguCd) {
            this.signguCd = signguCd;
        }

        //  private int id;
        @Element(name = "signguCd")
        private String signguCd;
    }


    public static class CmmMsgHeader {
        @Element(name = "requestMsgId", required = false)
        public String requestMsgId;
        @Element(name = "responseMsgId", required = false)
        public String responseMsgId;

        @Element(name = "responseTime", required = false)
        public String responseTime;

        @Element(name = "successYN", required = false)
        public String successYN;
        @Element(name = "returnCode", required = false)
        public String returnCode;
        @Element(name = "errMsg", required = false)
        public String errMsg;

        public String getRequestMsgId() {
            return requestMsgId;
        }

        public void setRequestMsgId(String requestMsgId) {
            this.requestMsgId = requestMsgId;
        }

        public String getResponseMsgId() {
            return responseMsgId;
        }

        public void setResponseMsgId(String responseMsgId) {
            this.responseMsgId = responseMsgId;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }

        public String getResponseTime() {
            return responseTime;
        }

        public void setResponseTime(String responseTime) {
            this.responseTime = responseTime;
        }

        public String getSuccessYN() {
            return successYN;
        }

        public void setSuccessYN(String successYN) {
            this.successYN = successYN;
        }

        public String getReturnCode() {
            return returnCode;
        }

        public void setReturnCode(String returnCode) {
            this.returnCode = returnCode;
        }
    }


    public List<SigunguList> getSigunguList() {
        return sigunguList;
    }

    public void setSigunguList(List<SigunguList> sigunguList) {
        this.sigunguList = sigunguList;
    }

    public CmmMsgHeader getCmmMsgHeader() {
        return cmmMsgHeader;
    }

    public void setCmmMsgHeader(CmmMsgHeader cmmMsgHeader) {
        this.cmmMsgHeader = cmmMsgHeader;
    }


}

