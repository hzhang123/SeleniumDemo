package per.hao.utils;

public enum DataSourceType {
    EXCEL("excel"),
    CSV("csv"),
    POSTGRESQL("postgresql");

    private String value;

    private DataSourceType(String value) { this.value = value; }

    /**
     * @return value
     * */
    public String value() {
        return this.value;
    }

    /**
     * @return value
     * */
    @Override
    public String toString() {
        return this.value;
    }
}
