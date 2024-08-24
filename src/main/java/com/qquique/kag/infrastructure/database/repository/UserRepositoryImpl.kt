package com.qquique.kag.infrastructure.database.repository

import com.qquique.kag.domain.entity.User
import org.hibernate.HibernateException
import org.hibernate.SessionFactory
import org.hibernate.query.Query
import java.util.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class UserRepositoryImpl(private val sessionFactory: SessionFactory) : Repository<User> {
    private val logger : Logger = LogManager.getLogger(UserRepositoryImpl::class.java)

    override fun findAll(): List<User> {
        this.sessionFactory.openSession().use { session ->
            val criteriaQuery = session.criteriaBuilder.createQuery(User::class.java)
            criteriaQuery.from(User::class.java)
            val query: Query<User> = session.createQuery(criteriaQuery)
            return query.resultList
        }
    }

    override fun findById(id: Long): Optional<User> {
            this.sessionFactory.openSession().use { session ->
                return Optional.ofNullable(session.find(User::class.java, id))
            }
    }

    override fun deleteById(id: Long) {
        this.sessionFactory.openSession().use { session ->
            try {
                val user: User = session.find(User::class.java, id) ?: return
                session.beginTransaction()
                session.remove(user)
                session.transaction.commit()
            } catch (e: HibernateException) {
                session.transaction.rollback()
            } catch (e: Exception) {
                session.transaction.rollback()
            }
        }
    }

    override fun save(model: User): User {
        this.sessionFactory.openSession().use { session ->
            try {
                session.beginTransaction()
                val user = if (model.id == null) {
                    session.persist(model)
                    model
                } else session.merge(model)
                session.transaction.commit()
                return user
            } catch (e: HibernateException) {
                session.transaction.rollback()
                logger.error("Hibernate Error", e)
                throw e
            }
        }
    }
}