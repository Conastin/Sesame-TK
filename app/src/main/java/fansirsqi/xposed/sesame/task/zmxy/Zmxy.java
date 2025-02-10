package fansirsqi.xposed.sesame.task.zmxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fansirsqi.xposed.sesame.data.RuntimeInfo;
import fansirsqi.xposed.sesame.model.ModelFields;
import fansirsqi.xposed.sesame.model.ModelGroup;
import fansirsqi.xposed.sesame.model.modelFieldExt.BooleanModelField;
import fansirsqi.xposed.sesame.model.modelFieldExt.IntegerModelField;
import fansirsqi.xposed.sesame.task.ModelTask;
import fansirsqi.xposed.sesame.task.TaskCommon;
import fansirsqi.xposed.sesame.util.Log;
import fansirsqi.xposed.sesame.util.ThreadUtil;

public class Zmxy extends ModelTask {
    private static final String TAG = Zmxy.class.getSimpleName();
    private IntegerModelField lastExecutionInterval;
    private IntegerModelField sesameExceptionDelay;
    private BooleanModelField sesameTask;
    private BooleanModelField collectSesame;
    private BooleanModelField collectSesameWithOneClick;
    private BooleanModelField sesameScoreUpdate;
    private List<String> taskCodeList;
    
    @Override
    public String getName() {
        return "芝麻信用";
    }

    @Override
    public ModelGroup getGroup() {
        return ModelGroup.ZMXY;
    }

    @Override
    public String getIcon() {
        return "Zmxy.png";
    }

    @Override
    public ModelFields getFields() {
        ModelFields modelFields = new ModelFields();
        modelFields.addField(lastExecutionInterval = new IntegerModelField("lastExecutionInterval", "距上次执行间隔不小于（毫秒，默认24小时即1天执行1次）", 24 * 60 * 60 * 1000));
        modelFields.addField(sesameExceptionDelay = new IntegerModelField("sesameExceptionDelay", "异常时延时（毫秒，默认6小时）", 6 * 60 * 60 * 1000));
        modelFields.addField(sesameTask = new BooleanModelField("sesameTask", "芝麻粒信用任务", false));
        modelFields.addField(collectSesame = new BooleanModelField("collectSesame", "芝麻粒领取", false));
        modelFields.addField(collectSesameWithOneClick = new BooleanModelField("collectSesameWithOneClick", "芝麻粒领取使用一键收取", false));
        modelFields.addField(sesameScoreUpdate = new BooleanModelField("sesameScoreUpdate", "自动更新芝麻分", false));
        return modelFields;
    }

    @Override
    public Boolean check() {
        if (TaskCommon.IS_ENERGY_TIME) {
            Log.debug("Zmxy[check]", "IS_ENERGY_TIME跳过执行");
            return false;
        }
        long exceptionTime = RuntimeInfo.getInstance().getLong("zmxyExceptionTime", 0);
        long lastExecTime = RuntimeInfo.getInstance().getLong("zmxyLastExecTime", 0);
        long nowTime = System.currentTimeMillis();
        boolean exceptionTimeCheck = nowTime - exceptionTime >= sesameExceptionDelay.getValue();
        boolean lastExecTimeCheck = nowTime - lastExecTime >= lastExecutionInterval.getValue();
        if (!exceptionTimeCheck) {
            Log.debug("Zmxy[check]", "exceptionTimeCheck跳过执行");
            return false;
        }
        if (!lastExecTimeCheck) {
            Log.debug("Zmxy[check]", "lastExecTimeCheck跳过执行");
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        if (!checkSesameCanRun()) {
            RuntimeInfo.getInstance().put("zmxyExceptionTime", System.currentTimeMillis());
            Log.debug("Zmxy[run]", "checkSesameCanRun跳过执行");
            return;
        }
        if (sesameTask.getValue()) {
            if (!doAllAvailableSesameTask()) {
                RuntimeInfo.getInstance().put("zmxyExceptionTime", System.currentTimeMillis());
                Log.debug("Zmxy[run]", "doAllAvailableSesameTask执行失败，跳过执行");
                return;
            }
        }
        if (collectSesame.getValue()) {
            if (!collectSesame(collectSesameWithOneClick.getValue())) {
                RuntimeInfo.getInstance().put("zmxyExceptionTime", System.currentTimeMillis());
                Log.debug("Zmxy[run]", "collectSesame执行失败，跳过执行");
                return;
            }
        }
        if (sesameScoreUpdate.getValue()) {
            if (!updateSesameScore()) {
                RuntimeInfo.getInstance().put("zmxyExceptionTime", System.currentTimeMillis());
                Log.debug("Zmxy[run]", "updateSesameScore执行失败，跳过执行");
                return;
            }
        }
        RuntimeInfo.getInstance().put("zmxyLastExecTime", System.currentTimeMillis());
    }

    /**
     * 首页通用进入方法
     */
    private JSONObject queryHome() {
        JSONObject returnObj = new JSONObject();
        try {
            String s = ZmxyRpcCall.queryServiceCard();
            JSONObject jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[ServiceCard响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.queryServiceCard", "芝麻信用💳[ServiceCard响应失败]#" + s);
                returnObj.put("success", false);
                return returnObj;
            }
            s = ZmxyRpcCall.queryClientVersion();
            jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[ClientVersion响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.queryClientVersion", "芝麻信用💳[ClientVersion响应失败]#" + s);
                returnObj.put("success", false);
                return returnObj;
            }
            s = ZmxyRpcCall.calculate("ZM_CREDIT_WIDGET_MODAL");
            jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[calculate响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.calculate", "芝麻信用💳[ClientVersion响应失败]#" + s);
                returnObj.put("success", false);
                return returnObj;
            }
            s = ZmxyRpcCall.queryMinor();
            jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[queryMinor响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.queryMinor", "芝麻信用💳[ClientVersion响应失败]#" + s);
                returnObj.put("success", false);
                return returnObj;
            }
            s = ZmxyRpcCall.calculate("SHOUYE_TABS_MY_POPOVER_ENTRANCE");
            jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[calculate响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.calculate", "芝麻信用💳[ClientVersion响应失败]#" + s);
                returnObj.put("success", false);
                return returnObj;
            }
            s = ZmxyRpcCall.queryHome();
            jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[首页响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.queryHome", "芝麻信用💳[首页响应失败]#" + s);
                returnObj.put("success", false);
                return returnObj;
            }
            returnObj.put("success", true);
            returnObj.put("data", jsonObject);
            return returnObj;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查是否满足运行芝麻信用任务的条件
     * @return bool
     */
    private Boolean checkSesameCanRun() {
        try {
            JSONObject jsonObject = queryHome();
            if (!jsonObject.optBoolean("success")) {
                return false;
            }
            jsonObject = jsonObject.getJSONObject("data");
            JSONObject entrance = jsonObject.getJSONObject("entrance");
            if (!entrance.optBoolean("openApp")) {
                Log.other("芝麻信用💳[未开通芝麻信用]");
                return false;
            }
            return true;
        } catch (Throwable t) {
            Log.printStackTrace(TAG + ".checkSesameCanRun", t);
            return false;
        }
    }

    /**
     * 芝麻信用任务
     */
    private Boolean doAllAvailableSesameTask() {
        try {
            String s = ZmxyRpcCall.queryAvailableSesameTask();
            ThreadUtil.sleep(500);
            JSONObject jo = new JSONObject(s);
            if (jo.has("resData")) {
                jo = jo.getJSONObject("resData");
            }
            if (!jo.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[查询任务响应失败]#" + jo.getString("resultCode"));
                Log.error(TAG + ".doAllAvailableSesameTask.queryAvailableSesameTask", "芝麻信用💳[查询任务响应失败]#" + s);
                return false;
            }
            JSONObject taskObj = jo.getJSONObject("data");
            taskCodeList = new ArrayList<>();
            if (taskObj.has("dailyTaskListVO")) {
                joinAndFinishSesameTask(taskObj.getJSONObject("dailyTaskListVO").getJSONArray("waitCompleteTaskVOS"));
                joinAndFinishSesameTask(taskObj.getJSONObject("dailyTaskListVO").getJSONArray("waitJoinTaskVOS"));
            }
            if (taskObj.has("toCompleteVOS")) {
                joinAndFinishSesameTask(taskObj.getJSONArray("toCompleteVOS"));
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG + ".doAllAvailableSesameTask", t);
            return false;
        }
        return true;
    }

    /**
     * 芝麻信用-领取并完成任务
     * @param taskList 任务列表
     * @throws JSONException JSON解析异常，上抛处理
     */
    private void joinAndFinishSesameTask(JSONArray taskList) throws JSONException {
        for (int i = 0; i < taskList.length(); i++) {
            JSONObject task = taskList.getJSONObject(i);
            String taskTemplateId = task.getString("templateId");
            String taskTitle = task.getString("title");
            int needCompleteNum = task.getInt("needCompleteNum");
            int completedNum = task.optInt("completedNum", 0);
            if (taskCodeList.contains(taskTemplateId)) {
                continue;
            }
            taskCodeList.add(taskTemplateId);
            String s;
            String recordId;
            JSONObject responseObj;
            if (task.getString("actionUrl").contains("jumpAction")) {
                // 跳转APP任务 依赖跳转的APP发送请求鉴别任务完成 仅靠hook支付宝无法完成
                continue;
            }
            if (!task.has("todayFinish")) {
                // 领取任务
                Log.debug("Zmxy[joinAndFinishSesameTask]", taskTitle + " 领取任务" + taskTemplateId);
                s = ZmxyRpcCall.joinSesameTask(taskTemplateId);
                ThreadUtil.sleep(200);
                responseObj = new JSONObject(s);
                if (!responseObj.optBoolean("success")) {
                    Log.other(TAG, "芝麻信用💳[领取任务" + taskTitle + "失败]#" + s);
                    Log.error(TAG + ".joinAndFinishSesameTask.joinSesameTask", "芝麻信用💳[领取任务" + taskTitle + "失败]#" + s);
                    continue;
                }
                recordId = responseObj.getJSONObject("data").getString("recordId");
            } else {
                if (!task.has("recordId")) {
                    Log.other(TAG, "芝麻信用💳[任务" + taskTitle + "未获取到recordId]#" + task);
                    Log.error(TAG + ".joinAndFinishSesameTask", "芝麻信用💳[任务" + taskTitle + "未获取到recordId]#" + task);
                    continue;
                }
                recordId = task.getString("recordId");
            }
            s = ZmxyRpcCall.feedBackSesameTask(taskTemplateId);
            ThreadUtil.sleep(200);
            responseObj = new JSONObject(s);
            if (!responseObj.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[任务" + taskTitle + "回调失败]#" + responseObj.getString("errorMessage"));
                Log.error(TAG + ".joinAndFinishSesameTask.feedBackSesameTask", "芝麻信用💳[任务" + taskTitle + "回调失败]#" + s);
                continue;
            }
            // 无法完成的任务
            switch (taskTemplateId) {
                case "save_ins_universal_new": // 坚持攒保证金
                case "xiaofeijin_visit_new": // 坚持攒消费金金币
                case "xianyonghoufu_new": // 体验先用后付
                    continue;
            }
            // 是否为浏览15s任务
            boolean assistiveTouch = task.getJSONObject("strategyRule").optBoolean("assistiveTouch");
            if (task.optBoolean("jumpToPushModel") || assistiveTouch) {
                s = ZmxyRpcCall.finishSesameTask(recordId);
                ThreadUtil.sleep(16000);
                responseObj = new JSONObject(s);
                if (!responseObj.optBoolean("success")) {
                    Log.other(TAG, "芝麻信用💳[任务" + taskTitle + "完成失败]#" + s);
                    Log.error(TAG + ".joinAndFinishSesameTask.finishSesameTask", "芝麻信用💳[任务" + taskTitle + "完成失败]#" + s);
                    continue;
                }
            }
            Log.other("芝麻信用💳[完成任务" + taskTitle + "]#(" + (completedNum + 1) + "/" + needCompleteNum + "天)");
        }
    }
    
    /**
     * 芝麻粒收取
     * @param withOneClick 启用一键收取
     */
    private Boolean collectSesame(Boolean withOneClick) {
        try {
            JSONObject jo = new JSONObject(ZmxyRpcCall.queryCreditFeedback());
            ThreadUtil.sleep(500);
            if (!jo.optBoolean("success")) {
                Log.other(TAG, "芝麻信用💳[查询未领取芝麻粒响应失败]#" + jo.getString("resultView"));
                Log.error(TAG + ".collectSesame.queryCreditFeedback", "芝麻信用💳[查询未领取芝麻粒响应失败]#" + jo);
                return false;
            }
            JSONArray availableCollectList = jo.getJSONArray("creditFeedbackVOS");
            if (withOneClick) {
                ThreadUtil.sleep(2000);
                jo = new JSONObject(ZmxyRpcCall.collectAllCreditFeedback());
                ThreadUtil.sleep(2000);
                if (!jo.optBoolean("success")) {
                    Log.other(TAG, "芝麻信用💳[一键收取芝麻粒响应失败]#" + jo);
                    Log.error(TAG + ".collectSesame.collectAllCreditFeedback", "芝麻信用💳[一键收取芝麻粒响应失败]#" + jo);
                    return false;
                }
            }
            for (int i = 0; i < availableCollectList.length(); i++) {
                jo = availableCollectList.getJSONObject(i);
                if (!"UNCLAIMED".equals(jo.getString("status"))) {
                    continue;
                }
                String title = jo.getString("title");
                String creditFeedbackId = jo.getString("creditFeedbackId");
                String potentialSize = jo.getString("potentialSize");
                if (!withOneClick) {
                    jo = new JSONObject(ZmxyRpcCall.collectCreditFeedback(creditFeedbackId));
                    ThreadUtil.sleep(2000);
                    if (!jo.optBoolean("success")) {
                        Log.other(TAG, "芝麻信用💳[查询未领取芝麻粒响应失败]#" + jo.getString("resultView"));
                        Log.error(TAG + ".collectSesame.collectCreditFeedback", "芝麻信用💳[收取芝麻粒响应失败]#" + jo);
                        return false;
                    }
                }
                Log.other("芝麻信用💳[" + title + "]#" + potentialSize + "粒" + (withOneClick ? "(一键收取)" : ""));
            }
        } catch (Throwable t) {
            Log.printStackTrace(TAG + ".collectSesame", t);
            return false;
        }
        return true;
    }

    /**
     * 更新芝麻分
     */
    private Boolean updateSesameScore() {
        try {
            JSONObject jsonObject = queryHome();
            if (!jsonObject.optBoolean("success")) {
                return false;
            }
            jsonObject = jsonObject.getJSONObject("data");
            if (!jsonObject.has("scoreRemind")) {
                return true;
            }
            long previousScore = jsonObject.getJSONObject("score").getLong("score");
            if ("待更新".equals(jsonObject.getJSONObject("scoreRemind").getJSONObject("extInfo").getString("scoreLabel"))) {
                String s = ZmxyRpcCall.queryMyScoreOverviewInfo();
                jsonObject = new JSONObject(s);
                if (!jsonObject.optBoolean("success")) {
                    Log.other(TAG, "芝麻信用💳[查询芝麻分信息失败]#" + jsonObject.optString("errorMsg"));
                    Log.error(TAG + ".updateSesameScore.queryMyScoreOverviewInfo", "芝麻信用💳[查询芝麻分信息失败]#" + s);
                    return false;
                }
                s = ZmxyRpcCall.calculate("zm_account_upgrade_CY24_66");
                jsonObject = new JSONObject(s);
                if (!jsonObject.optBoolean("success")) {
                    Log.other(TAG, "芝麻信用💳[calculate响应失败]#" + jsonObject.optString("errorMsg"));
                    Log.error(TAG + ".updateSesameScore.calculate", "芝麻信用💳[calculate响应失败]#" + s);
                    return false;
                }
                s = ZmxyRpcCall.evalActiveScoreWeekly();
                jsonObject = new JSONObject(s);
                if (!jsonObject.optBoolean("success")) {
                    Log.other(TAG, "芝麻信用💳[更新芝麻分失败]#" + jsonObject.optString("errorMsg"));
                    Log.error(TAG + ".updateSesameScore.calculate", "芝麻信用💳[更新芝麻分失败]#" + s);
                    return false;
                }
                jsonObject = jsonObject.getJSONObject("activeScoreWeeklyVO");
                long afterScore = jsonObject.getLong("score");
                Log.other(TAG, "芝麻信用💳[更新芝麻分]#" + previousScore + " -> " + afterScore);
            }
        } catch (Exception e) {
            Log.printStackTrace(e);
            return false;
        }
        return true;
    }
}
