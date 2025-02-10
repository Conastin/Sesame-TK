package fansirsqi.xposed.sesame.task.wealthGold;

import fansirsqi.xposed.sesame.hook.RequestManager;

public class WealthGoldRpcCall {
    public static String needleV2Index() {
        return RequestManager.requestString("com.alipay.wealthgoldtwa.needle.v2.index", "[{\"bizScene\":\"gold\",\"chInfo\":\"gold\",\"forceNewVersion\":0,\"taskId\":\"\"}]");
    }

    public static String wealthwisdomISophonRender() {
        return RequestManager.requestString("com.alipay.wealthgoldtwa.needle.wealthwisdomISophonRender", "[{\"blockInfoList\":[{\"blockCode\":\"GOLDBILL_WEALTH_WISDOM_MODAL\"}]}]");
    }

    public static String collect() {
        return RequestManager.requestString("com.alipay.wealthgoldtwa.goldbill.v2.index.collect", "[{\"campId\":\"CP1417744\",\"directModeDisableCollect\":1,\"from\":\"antfarm\",\"trigger\":\"Y\"}]");
    }
}
