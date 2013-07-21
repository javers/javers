package pl.edu.icm.sedno.common.util;

import org.springframework.beans.factory.FactoryBean;

public class NullFactoryBean implements FactoryBean<Void> {

        public Void getObject() throws Exception {
            return null;
        }

        public Class<? extends Void> getObjectType() {
            return null;
        }

        public boolean isSingleton() {
            return true;
        }
    }
