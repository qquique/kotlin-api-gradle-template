package com.qquique.kag.infrastructure.database.config

import org.hibernate.cfg.Configuration

class XmlConfigurationProvider : ConfigurationProvider {
    override fun getConfiguration(): Configuration {
        return Configuration().configure()
    }
}