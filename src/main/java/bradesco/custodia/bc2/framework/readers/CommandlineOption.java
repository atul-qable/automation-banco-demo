package bradesco.custodia.bc2.framework.readers;

import com.beust.jcommander.Parameter;

public class CommandlineOption {


    @Parameter(
            names = {"--config","-c"},
            description = "Configuration to run",
            required = false
    )
    private String configName;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Parameter(
            names = {"--path","-p"},
            description = "Path for Configuration File",
            required = true
    )
    private String path;

    public String getPathForConfigurationFile() {
        return path;
    }

    public void setPathForConfigurationFile(String path) {
        this.path = path;
    }

}
