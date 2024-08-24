package com.qquique.kag.infrastructure.database.config

import org.hibernate.cfg.Configuration

interface ConfigurationProvider {
    fun getConfiguration(): Configuration
}