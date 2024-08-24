package com.qquique.kag.infrastructure.database.config

import org.hibernate.cfg.Configuration

class HibernateConfigurationProvider : ConfigurationProvider {
    override fun getConfiguration(): Configuration {
        val configuration: Configuration = Configuration().apply {
            setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/kag");
            setProperty("hibernate.connection.username", "postgres");
            setProperty("hibernate.connection.password", "password");
            setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        }
        return configuration
    }
}