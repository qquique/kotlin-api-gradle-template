package com.qquique.kag.infrastructure.database

import com.qquique.kag.infrastructure.database.config.ConfigurationProvider
import com.qquique.kag.infrastructure.database.config.XmlConfigurationProvider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.hibernate.SessionFactory
import javax.xml.crypto.Data

class Database(private val sessionFactory: SessionFactory) {
    val logger: Logger = LogManager.getLogger(Database::class.java)

    fun getSessionFactory(): SessionFactory = this.sessionFactory
    fun close() = sessionFactory.close()

    companion object {
        fun createWithXmlConfiguration(): Database {
            val configurationProvider: ConfigurationProvider = XmlConfigurationProvider()
            return Database(configurationProvider.getConfiguration().buildSessionFactory())
        }
    }
}