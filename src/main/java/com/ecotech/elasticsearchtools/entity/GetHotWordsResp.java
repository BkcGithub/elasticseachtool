package com.ecotech.elasticsearchtools.entity;

import java.util.List;

public class GetHotWordsResp {
    private int totalCount;
    private List<HotWord> hotWords;

    public class HotWord {
        private String url;
        private int count;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<HotWord> getHotWords() {
        return hotWords;
    }

    public void setHotWords(List<HotWord> hotWords) {
        this.hotWords = hotWords;
    }

}
