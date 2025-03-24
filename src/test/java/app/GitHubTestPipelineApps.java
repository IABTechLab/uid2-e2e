package app;

import common.EnvUtil;
import app.component.Core;
import app.component.Operator;

import java.util.Set;

public class GitHubTestPipelineApps extends Apps {
    private static final String OPERATOR_URL = EnvUtil.getEnv("UID2_E2E_PIPELINE_OPERATOR_URL");
    private static final Operator.Type OPERATOR_TYPE = Operator.Type.valueOf(EnvUtil.getEnv("UID2_E2E_PIPELINE_OPERATOR_TYPE"));
    private static final Operator.CloudProvider OPERATOR_CLOUD_PROVIDER = Operator.CloudProvider.valueOf(EnvUtil.getEnv("UID2_E2E_PIPELINE_OPERATOR_CLOUD_PROVIDER"));

    private static final String CORE_URL = EnvUtil.getEnv("UID2_E2E_CORE_URL");
    private static final String OPERATOR_NAME = OPERATOR_TYPE == Operator.Type.PUBLIC
            ? "GitHub Test Pipeline - Public Operator"
            : "GitHub Test Pipeline - Private %s Operator".formatted(OPERATOR_CLOUD_PROVIDER.toString());

    public GitHubTestPipelineApps() {
        super(Set.of(
                new Operator(OPERATOR_URL, OPERATOR_NAME, OPERATOR_TYPE),
                new Core(CORE_URL, "GitHub Test Pipeline - Core")
        ));
    }
}
