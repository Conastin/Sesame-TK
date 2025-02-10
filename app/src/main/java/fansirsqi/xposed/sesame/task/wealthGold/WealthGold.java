package fansirsqi.xposed.sesame.task.wealthGold;

import org.json.JSONArray;
import org.json.JSONObject;

import fansirsqi.xposed.sesame.data.RuntimeInfo;
import fansirsqi.xposed.sesame.model.ModelFields;
import fansirsqi.xposed.sesame.model.ModelGroup;
import fansirsqi.xposed.sesame.model.modelFieldExt.BooleanModelField;
import fansirsqi.xposed.sesame.model.modelFieldExt.IntegerModelField;
import fansirsqi.xposed.sesame.task.ModelTask;
import fansirsqi.xposed.sesame.task.TaskCommon;
import fansirsqi.xposed.sesame.task.zmxy.Zmxy;
import fansirsqi.xposed.sesame.util.Log;

public class WealthGold extends ModelTask {
    private static final String TAG = Zmxy.class.getSimpleName();
    @Override
    public String getName() {
        return "黄金票";
    }

    @Override
    public ModelGroup getGroup() {
        return ModelGroup.WEALTH_GOLD;
    }

    @Override
    public String getIcon() {
        return "WealthGold.png";
    }

    private BooleanModelField wealthGoldSignIn;
    private IntegerModelField lastExecutionInterval;
    private IntegerModelField wealthGoldExceptionDelay;

    @Override
    public ModelFields getFields() {
        ModelFields modelFields = new ModelFields();
        modelFields.addField(wealthGoldSignIn = new BooleanModelField("wealthGoldSignIn", "黄金票签到", false));
        modelFields.addField(lastExecutionInterval = new IntegerModelField("lastExecutionInterval", "距上次执行间隔不小于（毫秒，默认24小时即1天执行1次）", 24 * 60 * 60 * 1000));
        modelFields.addField(wealthGoldExceptionDelay = new IntegerModelField("wealthGoldExceptionDelay", "异常时延时（毫秒，默认6小时）", 6 * 60 * 60 * 1000));

        return modelFields;
    }

    @Override
    public Boolean check() {
        if (TaskCommon.IS_ENERGY_TIME) {
            Log.debug("WealthGold[check]", "IS_ENERGY_TIME跳过执行");
            return false;
        }
        long exceptionTime = RuntimeInfo.getInstance().getLong("wealthGoldExceptionTime", 0);
        long lastExecTime = RuntimeInfo.getInstance().getLong("wealthGoldLastExecTime", 0);
        long nowTime = System.currentTimeMillis();
        boolean exceptionTimeCheck = nowTime - exceptionTime >= wealthGoldExceptionDelay.getValue();
        boolean lastExecTimeCheck = nowTime - lastExecTime >= lastExecutionInterval.getValue();
        if (!exceptionTimeCheck) {
            Log.debug("WealthGold[check]", "exceptionTimeCheck跳过执行");
            return false;
        }
        if (!lastExecTimeCheck) {
            Log.debug("WealthGold[check]", "lastExecTimeCheck跳过执行");
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        if (wealthGoldSignIn.getValue()) {
            if (!wealthGoldSignIn()) {
                RuntimeInfo.getInstance().put("wealthGoldExceptionTime", System.currentTimeMillis());
                Log.debug("WealthGold[run]", "wealthGoldSignIn执行失败，跳过执行");
                return;
            }
        }
        RuntimeInfo.getInstance().put("wealthGoldLastExecTime", System.currentTimeMillis());
    }

    public boolean wealthGoldSignIn() {
        try {
            String s = WealthGoldRpcCall.needleV2Index();
            JSONObject jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "黄金票\uD83C\uDFAB[needleV2Index响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.queryServiceCard", "黄金票\uD83C\uDFAB[needleV2Index响应失败]#" + s);
                return false;
            }
            s = WealthGoldRpcCall.wealthwisdomISophonRender();
            jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "黄金票\uD83C\uDFAB[needleV2Index响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.queryServiceCard", "黄金票\uD83C\uDFAB[needleV2Index响应失败]#" + s);
                return false;
            }
            s = WealthGoldRpcCall.collect();
            jsonObject = new JSONObject(s);
            if (!jsonObject.optBoolean("success")) {
                Log.other(TAG, "黄金票\uD83C\uDFAB[collect响应失败]#" + jsonObject.optString("errorMsg"));
                Log.error(TAG + ".queryHome.queryServiceCard", "黄金票\uD83C\uDFAB[collect响应失败]#" + s);
                return false;
            }
            jsonObject = jsonObject.getJSONObject("result");
            JSONArray collectedList = jsonObject.getJSONArray("collectedList");
            for (int i = 0; i < collectedList.length(); i++) {
                String name = (String) collectedList.get(i);
                if (name.contains("天天领黄金票")) {
                    Log.other(TAG, "黄金票\uD83C\uDFAB[签到]#" + name);
                } else {
                    Log.other(TAG, "黄金票\uD83C\uDFAB[收集]#" + name);
                }
            }
        } catch (Exception e) {
            Log.printStackTrace(TAG + ".wealthGoldSignIn", e);
            return false;
        }
        return true;
    }
}
