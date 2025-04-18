package com.cheestree.vetly.converter

import com.cheestree.vetly.domain.exception.VetException.BadRequestException
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.http.RequestExtraDataTypeRegistry
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class RequestCreateInputModelDeserializer : StdDeserializer<RequestCreateInputModel>(RequestCreateInputModel::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): RequestCreateInputModel {
        val node: JsonNode = p.codec.readTree(p)
        val mapper = p.codec as ObjectMapper

        val action = RequestAction.valueOf(node.get("action").asText())
        val target = RequestTarget.valueOf(node.get("target").asText())
        val justification = node.get("justification").asText()
        val files = mapper.convertValue(node.get("files"), object : TypeReference<List<String>>() {})
        val extraDataNode = node.get("extraData")


        val extraData: RequestExtraData? = if (extraDataNode != null && !extraDataNode.isNull) {
            val expectedType = RequestExtraDataTypeRegistry.expectedTypeFor(target, action)
            try {
                mapper.treeToValue(extraDataNode, expectedType.java)
            } catch (ex: Exception) {
                throw BadRequestException("Invalid input data for $target $action: ${ex.message}")
            }
        } else null

        return RequestCreateInputModel(
            action = action,
            target = target,
            justification = justification,
            extraData = extraData,
            files = files
        )
    }
}