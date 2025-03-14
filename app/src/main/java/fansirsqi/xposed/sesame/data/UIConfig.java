package fansirsqi.xposed.sesame.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

import fansirsqi.xposed.sesame.ui.DemoSettingActivity;
import fansirsqi.xposed.sesame.ui.NewSettingsActivity;
import fansirsqi.xposed.sesame.ui.SettingsActivity;
import fansirsqi.xposed.sesame.util.Files;
import fansirsqi.xposed.sesame.util.JsonUtil;
import fansirsqi.xposed.sesame.util.Log;
import lombok.Data;
import lombok.Setter;

@Data
public class UIConfig {
    private static final String TAG = UIConfig.class.getSimpleName();
    public static final UIConfig INSTANCE = new UIConfig();

    @JsonIgnore // 排除掉不需要序列化的字段
    private boolean init;


    public static final String UI_OPTION_OLD = "old";
    public static final String UI_OPTION_NEW = "new";
    public static final String UI_OPTION_TEST = "test";

    @Setter
    @JsonProperty("uiOption") // 直接序列化 uiOption 字段
    private String uiOption = UI_OPTION_NEW; // 默认值为 "new"

    private UIConfig() {
    }

    public static Boolean save() {
        Log.record("保存UI配置");
        return Files.setUIConfigFile(JsonUtil.formatJson(INSTANCE));
    }

    public static synchronized UIConfig load() {
        File uiConfigFile = Files.getUIConfigFile();
        try {
            if (uiConfigFile.exists()) {
                String json = Files.readFromFile(uiConfigFile);
                if (!json.trim().isEmpty()) {
                    JsonUtil.copyMapper().readerForUpdating(INSTANCE).readValue(json);
                    String formatted = JsonUtil.formatJson(INSTANCE);
                    if (formatted != null && !formatted.equals(json)) {
                        Log.runtime(TAG, "格式化UI配置");
                        Files.write2File(formatted, uiConfigFile);
                    }
                } else {
                    Log.runtime(TAG, "配置文件为空，使用默认配置");
                    resetToDefault();
                }
            } else {
                Log.runtime(TAG, "配置文件不存在，初始化默认配置");
                resetToDefault();
                Files.write2File(JsonUtil.formatJson(INSTANCE), uiConfigFile);
            }
        } catch (Exception e) {
            Log.printStackTrace(TAG, e);
            Log.runtime(TAG, "重置UI配置");
            resetToDefault();
            try {
                Files.write2File(JsonUtil.formatJson(INSTANCE), uiConfigFile);
            } catch (Exception e2) {
                Log.printStackTrace(TAG, e2);
            }
        }
        INSTANCE.setInit(true);
        return INSTANCE;
    }

    private static synchronized void resetToDefault() {
        INSTANCE.setUiOption(UI_OPTION_NEW); // 默认设置为 "new"
        INSTANCE.setInit(false);
    }

    @JsonIgnore
    public Class<?> getTargetActivityClass() {
        return switch (uiOption) {
            case UI_OPTION_OLD -> SettingsActivity.class;
            case UI_OPTION_NEW -> NewSettingsActivity.class;
            case UI_OPTION_TEST -> DemoSettingActivity.class;
            default -> {
                Log.runtime(TAG, "未知的 UI 选项: " + uiOption);
                yield NewSettingsActivity.class;
            }
        };
    }
}
