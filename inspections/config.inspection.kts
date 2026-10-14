import com.intellij.jvm.dfa.analysis.dev.config.TaintConfig
import com.intellij.jvm.dfa.analysis.configurator.taint.rules.TaintRule

TaintConfig {
    sanitize(
        "org.springframework.test.web.servlet.ResultActions.andExpect",
        0,
        true,
        "org.springframework.test.web.servlet.ResultActions",
        TaintRule.allRulesList()
    )
}