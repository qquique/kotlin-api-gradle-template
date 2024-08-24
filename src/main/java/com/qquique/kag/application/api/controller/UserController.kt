package com.qquique.kag.application.api.controller

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.qquique.kag.application.dto.UserDTO
import com.qquique.kag.application.service.UserService
import com.qquique.kag.infrastructure.database.Database
import com.qquique.kag.infrastructure.database.repository.UserRepositoryImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@WebServlet("/api/user/*")
class UserController : HttpServlet() {
    private val userService: UserService =
        UserService(UserRepositoryImpl(Database.createWithXmlConfiguration().getSessionFactory()))
    private val logger = KotlinLogging.logger {}

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val reqNotNull = requireNotNull(req) { "HttpServletRequest is required" }
        val respNotNull = requireNotNull(resp) { "HttpServletResponse is required" }
        val objectMapper = ObjectMapper().registerKotlinModule()
        respNotNull.contentType = "application/json"
        val pathInfo = reqNotNull.pathInfo ?: "/"
        when {
            pathInfo == "/" -> handleGetAllUsers(respNotNull, objectMapper)
            pathInfo.startsWith("/") -> handleGetUserById(pathInfo, respNotNull, objectMapper)
            else -> handleInvalidPath(respNotNull, objectMapper)
        }
    }

    private fun handleGetAllUsers(resp: HttpServletResponse, objectMapper: ObjectMapper) {
        try {
            val userDTOList = userService.getAllUsers()
            resp.status = HttpServletResponse.SC_OK
            resp.writer.write(objectMapper.writeValueAsString(userDTOList))
        } catch (e: Exception) {
            handleException(resp, objectMapper, e)
        }
    }

    private fun handleGetUserById(pathInfo: String, resp: HttpServletResponse, objectMapper: ObjectMapper) {
        try {
            val id = pathInfo.substring(1).toLongOrNull() ?: run {
                resp.status = HttpServletResponse.SC_BAD_REQUEST
                resp.writer.write(objectMapper.writeValueAsString("Invalid ID format"))
                return
            }

            val userDTO = userService.getUserById(id)
            if (userDTO != null) {
                resp.status = HttpServletResponse.SC_OK
                resp.writer.write(objectMapper.writeValueAsString(userDTO))
            } else {
                resp.status = HttpServletResponse.SC_NOT_FOUND
                resp.writer.write(objectMapper.writeValueAsString("User not found"))
            }
        } catch (e: Exception) {
            handleException(resp, objectMapper, e)
        }
    }

    private fun handleException(resp: HttpServletResponse, objectMapper: ObjectMapper, e: Exception) {
        resp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        val errorMessage = "An error occurred: ${e.message}"
        logger.error { errorMessage }
        resp.writer.write(objectMapper.writeValueAsString(errorMessage))
    }

    private fun handleInvalidPath(resp: HttpServletResponse, objectMapper: ObjectMapper) {
        resp.status = HttpServletResponse.SC_BAD_REQUEST
        resp.writer.write(objectMapper.writeValueAsString("Invalid path"))
    }

    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val reqNotNull = requireNotNull(req) { "HttpServletRequest is required" }
        val respNotNull = requireNotNull(resp) { "HttpServletResponse is required" }
        respNotNull.contentType = "application/json"
        val objectMapper = ObjectMapper().registerKotlinModule()
        try {
            var userDTO: UserDTO = objectMapper.readValue(reqNotNull.reader, UserDTO::class.java)
            userDTO = userService.createUser(userDTO)
            respNotNull.status = HttpServletResponse.SC_OK
            respNotNull.writer.write(objectMapper.writeValueAsString(userDTO))
        } catch (e: JsonProcessingException) {
            respNotNull.status = HttpServletResponse.SC_BAD_REQUEST
            respNotNull.writer.write("""{"error": "Invalid JSON format"}""")
        } catch (e: Exception) {
            handleException(respNotNull, objectMapper, e)
        }
    }

    override fun doPut(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val reqNotNull = requireNotNull(req) { "HttpServletRequest is required" }
        val respNotNull = requireNotNull(resp) { "HttpServletResponse is required" }
        val objectMapper = ObjectMapper().registerKotlinModule()
        respNotNull.contentType = "application/json"

        var userDTO = objectMapper.readValue(reqNotNull.reader, UserDTO::class.java)
        try {
            userDTO = userService.updateUser(userDTO.id, userDTO)
            if (userDTO != null) {
                respNotNull.status = HttpServletResponse.SC_OK
                respNotNull.writer.write(objectMapper.writeValueAsString(userDTO))
            } else {
                logger.error { "error updating " }
                respNotNull.status = HttpServletResponse.SC_NOT_FOUND
                respNotNull.writer.write(objectMapper.writeValueAsString("error updating"))
            }
        } catch (e: JsonProcessingException) {
            respNotNull.status = HttpServletResponse.SC_BAD_REQUEST
            respNotNull.writer.write("""{"error": "Invalid JSON format"}""")
        } catch (e: Exception) {
            handleException(respNotNull, objectMapper, e)
        }
    }

    override fun doDelete(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val reqNotNull = requireNotNull(req) { "HttpServletRequest is required" }
        val respNotNull = requireNotNull(resp) { "HttpServletResponse is required" }
        val objectMapper = ObjectMapper().registerKotlinModule()
        respNotNull.contentType = "application/json"
        val id = extractIdFromPath(reqNotNull)
        try {
            userService.deleteUser(id)
            respNotNull.status = HttpServletResponse.SC_OK
            respNotNull.writer.write(objectMapper.writeValueAsString("record deleted"))
        } catch (e: Exception) {
            logger.error { "error deleting ${e.message}" }
            respNotNull.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            respNotNull.writer.write("""{"error": "${e.message}"}""")
        }
    }

    private fun extractIdFromPath(req: HttpServletRequest): Long {
        return req.pathInfo.substring(1).toLongOrNull() ?: throw IllegalArgumentException("Invalid ID in path")
    }

}