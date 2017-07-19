package com.ecotech.elasticsearchtools.entity;

import java.util.List;

public class GetSemInquiryResp {
    private List<SemInquiry> semInquiries;

    public class SemInquiry {
        String url;
        String placeId;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }
    }

    public List<SemInquiry> getSemInquiries() {
        return semInquiries;
    }

    public void setSemInquiries(List<SemInquiry> semInquiries) {
        this.semInquiries = semInquiries;
    }

}
