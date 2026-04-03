package com.findu.common.cache;

/**
 * 币种信息 DTO（来自 findu-trade 的 GET /api/v1/public/currencies 接口）。
 */
public class CurrencyInfo {

    private String code;

    private String symbol;

    private String nameZh;

    private String nameEn;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}
