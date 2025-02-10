package fansirsqi.xposed.sesame.task.zmxy;

import fansirsqi.xposed.sesame.hook.RequestManager;

public class ZmxyRpcCall {

    /**
     * 查询服务状态
     */
    public static String queryMinor() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.home.api.HomeV7RpcManager.queryMinor", "[{\"invokeSource\":\"zmHome\"}]");
    }

    public static String queryServiceCard() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.home.api.HomeV7RpcManager.queryServiceCard", "[{}]");
    }

    public static String queryClientVersion() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.home.api.HomeV6RpcManager.queryClientVersion", "[{}]");
    }

    public static String calculate(String sceneCode) {
        return RequestManager.requestString("com.antgroup.zmxy.zmmemberop.biz.rpc.rulecal.RuleCalculateRpcManager.calculate", "[{\"params\":{},\"sceneCode\":\"" + sceneCode + "\"}]");
    }

    /**
     * 芝麻信用首页
     */
    public static String queryHome() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.home.api.HomeV7RpcManager.queryHome", "[{\"invokeSource\":\"zmHome\",\"miniZmGrayInside\":\"\",\"version\":\"week\"}]");
    }

    /**
     * 获取芝麻信用任务列表
     */
    public static String queryAvailableSesameTask() {
        return RequestManager.requestString("com.antgroup.zmxy.zmmemberop.biz.rpc.creditaccumulate.CreditAccumulateStrategyRpcManager.queryListV3", "[{}]");
    }

    /**
     * 芝麻信用领取任务
     */
    public static String joinSesameTask(String taskTemplateId) {
        return RequestManager.requestString("com.antgroup.zmxy.zmmemberop.biz.rpc.promise.PromiseRpcManager.joinActivity", "[{\"chInfo\":\"seasameList\",\"joinFromOuter\":false,\"templateId\":\"" + taskTemplateId + "\"}]");
    }

    /**
     * 芝麻信用获取任务回调
     */
    public static String feedBackSesameTask(String taskTemplateId) {
        return RequestManager.requestString("com.antgroup.zmxy.zmmemberop.biz.rpc.creditaccumulate.CreditAccumulateStrategyRpcManager.taskFeedback", "[{\"actionType\":\"TO_COMPLETE\",\"templateId\":\"" + taskTemplateId + "\"}]", "zmmemberop", "taskFeedback", "CreditAccumulateStrategyRpcManager");
    }

    /**
     * 芝麻信用完成任务
     */
    public static String finishSesameTask(String recordId) {
        return RequestManager.requestString("com.antgroup.zmxy.zmmemberop.biz.rpc.promise.PromiseRpcManager.pushActivity", "[{\"recordId\":\"" + recordId + "\"}]");
    }

    /**
     * 查询可收取的芝麻粒
     */
    public static String queryCreditFeedback() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.home.creditaccumulate.api.CreditAccumulateRpcManager.queryCreditFeedback", "[{\"queryPotential\":false,\"size\":20,\"status\":\"UNCLAIMED\"}]");
    }

    /**
     * 一键收取芝麻粒
     */
    public static String collectAllCreditFeedback() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.home.creditaccumulate.api.CreditAccumulateRpcManager.collectCreditFeedback", "[{\"collectAll\":true,\"status\":\"UNCLAIMED\"}]");
    }

    /**
     * 收取芝麻粒
     *
     * @param creditFeedbackId creditFeedbackId
     */
    public static String collectCreditFeedback(String creditFeedbackId) {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.home.creditaccumulate.api.CreditAccumulateRpcManager.collectCreditFeedback", "[{\"collectAll\":false,\"creditFeedbackId\":\"" + creditFeedbackId + "\",\"status\":\"UNCLAIMED\"}]");
    }

    /**
     * 查询芝麻分信息
     */
    public static String queryMyScoreOverviewInfo() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.growthengine.CreditScoreRpcManager.queryMyScoreOverviewInfo", "[{\"invokeSource\":\"zmScore\"}]");
    }

    /**
     * 每周更新芝麻分
     */
    public static String evalActiveScoreWeekly() {
        return RequestManager.requestString("com.antgroup.zmxy.zmcustprod.biz.rpc.activescore.api.evalActiveScoreWeekly", "[{}]");
    }
}
