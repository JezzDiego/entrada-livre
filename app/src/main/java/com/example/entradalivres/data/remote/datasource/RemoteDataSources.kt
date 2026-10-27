package com.example.entradalivres.data.remote.datasource

import com.example.entradalivres.data.remote.api.EstablishmentApi
import com.example.entradalivres.data.remote.api.ReviewApi
import com.example.entradalivres.data.remote.api.UserApi
import com.example.entradalivres.data.remote.dto.EstablishmentDTO
import com.example.entradalivres.data.remote.dto.ReviewDTO
import com.example.entradalivres.data.remote.dto.UserDTO

/*
 * Datasources remotos: contratos prontos para a futura API.
 * Ainda NÃO são utilizados pelos repositórios — o app funciona 100% offline por enquanto.
 */

class EstablishmentRemoteDataSource(private val api: EstablishmentApi) {

    suspend fun getEstablishments(): List<EstablishmentDTO> = api.getEstablishments()

    suspend fun getEstablishmentById(id: String): EstablishmentDTO = api.getEstablishmentById(id)

    suspend fun search(query: String, filters: List<String>): List<EstablishmentDTO> =
        api.search(query, filters)
}

class ReviewRemoteDataSource(private val api: ReviewApi) {

    suspend fun getRecentReviews(limit: Int): List<ReviewDTO> = api.getRecentReviews(limit)

    suspend fun getReviewsByEstablishment(establishmentId: String): List<ReviewDTO> =
        api.getReviewsByEstablishment(establishmentId)

    suspend fun publishReview(review: ReviewDTO) = api.publishReview(review)

    suspend fun markHelpful(reviewId: String) = api.markHelpful(reviewId)
}

class UserRemoteDataSource(private val api: UserApi) {

    suspend fun register(user: UserDTO): UserDTO = api.register(user)

    suspend fun update(user: UserDTO): UserDTO = api.update(user)
}
