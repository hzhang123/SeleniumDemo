package per.hao.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * page映射对象
 * */
public class PageInfo implements Serializable {
    private String pageName;
    private String url;
    private Map<String, LocatorInfo> locatorInfoMap= new HashMap<>();

    public PageInfo(String pageName, String url, Map<String, LocatorInfo> locatorInfoMap) {
        this.pageName = pageName;
        this.url = url;
        this.locatorInfoMap = locatorInfoMap;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLocatorInfoMap(Map<String, LocatorInfo> locatorInfoMap) {
        this.locatorInfoMap = locatorInfoMap;
    }

    public String getPageName() {
        return pageName;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, LocatorInfo> getLocatorInfoMap() {
        return locatorInfoMap;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageName='" + pageName + '\'' +
                ", url='" + url + '\'' +
                ", locatorInfoMap=" + locatorInfoMap +
                '}';
    }
}
