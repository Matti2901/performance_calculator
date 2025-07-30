package org.apache.roller.weblogger.business.search.lucene;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.roller.weblogger.config.WebloggerConfig;

import java.lang.reflect.InvocationTargetException;

public class AnalyzerUtils {
    private final static Log logger = LogFactory.getFactory().getInstance(AnalyzerUtils.class);
    static Analyzer instantiateAnalyzer() {
        final String className = WebloggerConfig.getProperty("lucene.analyzer.class");
        try {
            final Class<?> clazz = Class.forName(className);
            return (Analyzer) ConstructorUtils.invokeConstructor(clazz, null);
        } catch (final ClassNotFoundException e) {
           logger.error("failed to lookup analyzer class: " + className, e);
            return instantiateDefaultAnalyzer();
        } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException |
                       InvocationTargetException e) {
            logger.error("failed to instantiate analyzer: " + className, e);
            return instantiateDefaultAnalyzer();
        }
    }

    private static Analyzer instantiateDefaultAnalyzer() {
        return new StandardAnalyzer();
    }

    /**
     * This is the analyzer that will be used to tokenize comment text.
     *
     * @return Analyzer to be used in manipulating the database.
     */
    public static final Analyzer getAnalyzer() {
        return instantiateAnalyzer();
    }
}
