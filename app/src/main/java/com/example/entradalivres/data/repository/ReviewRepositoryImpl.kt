package com.example.entradalivres.data.repository

import com.example.entradalivres.data.local.datasource.PhotoLocalDataSource
import com.example.entradalivres.data.local.datasource.ReviewLocalDataSource
import com.example.entradalivres.data.mapper.toDomain
import com.example.entradalivres.data.mapper.toEntity
import com.example.entradalivres.data.remote.datasource.ReviewRemoteDataSource
import com.example.entradalivres.domain.model.Review
import com.example.entradalivres.domain.repository.ReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Relatos salvos apenas localmente por enquanto. O [remoteDS] fica injetado
 * para a futura publicação/sincronização com a API.
 */
class ReviewRepositoryImpl(
    private val localDS: ReviewLocalDataSource,
    private val photoDS: PhotoLocalDataSource,
    @Suppress("unused") private val remoteDS: ReviewRemoteDataSource,
) : ReviewRepository {

    override fun observeRecentReviews(limit: Int): Flow<List<Review>> =
        localDS.observeRecent(limit)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)

    override suspend fun getReviewsByEstablishment(establishmentId: String): List<Review> =
        withContext(Dispatchers.IO) { localDS.getByEstablishment(establishmentId).map { it.toDomain() } }

    override suspend fun getReviewsByAuthor(authorId: String): List<Review> =
        withContext(Dispatchers.IO) { localDS.getByAuthor(authorId).map { it.toDomain() } }

    override suspend fun addReview(review: Review) = withContext(Dispatchers.IO) {
        localDS.save(review.toEntity())
    }

    override suspend fun toggleHelpful(reviewId: String) = withContext(Dispatchers.IO) {
        val current = localDS.getById(reviewId) ?: return@withContext
        val marked = !current.markedHelpful
        val count = (current.helpfulCount + if (marked) 1 else -1).coerceAtLeast(0)
        localDS.updateHelpful(reviewId, count, marked)
    }

    override suspend fun savePhoto(sourceUri: String): String = withContext(Dispatchers.IO) {
        photoDS.save(sourceUri)
    }

    override suspend fun deletePhoto(path: String) = withContext(Dispatchers.IO) {
        photoDS.delete(path)
    }
}
