package com.example.entradalivres.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.entradalivres.data.local.dao.EstablishmentDao
import com.example.entradalivres.data.local.dao.ReviewDao
import com.example.entradalivres.data.local.dao.UserDao
import com.example.entradalivres.data.local.datasource.EstablishmentLocalDataSource
import com.example.entradalivres.data.local.datasource.PhotoLocalDataSource
import com.example.entradalivres.data.local.datasource.ReviewLocalDataSource
import com.example.entradalivres.data.local.datasource.UserLocalDataSource
import com.example.entradalivres.data.local.db.EntradaLivreDatabase
import com.example.entradalivres.data.local.preferences.EntradaLivrePreferences
import com.example.entradalivres.data.remote.api.EstablishmentApi
import com.example.entradalivres.data.remote.api.ReviewApi
import com.example.entradalivres.data.remote.api.UserApi
import com.example.entradalivres.data.remote.datasource.EstablishmentRemoteDataSource
import com.example.entradalivres.data.remote.datasource.ReviewRemoteDataSource
import com.example.entradalivres.data.remote.datasource.UserRemoteDataSource
import com.example.entradalivres.data.remote.network.RetrofitClient
import com.example.entradalivres.data.repository.EstablishmentRepositoryImpl
import com.example.entradalivres.data.repository.ReviewRepositoryImpl
import com.example.entradalivres.data.repository.UserRepositoryImpl
import com.example.entradalivres.domain.repository.EstablishmentRepository
import com.example.entradalivres.domain.repository.ReviewRepository
import com.example.entradalivres.domain.repository.UserRepository
import com.example.entradalivres.domain.usecase.establishment.*
import com.example.entradalivres.domain.usecase.review.*
import com.example.entradalivres.domain.usecase.user.*
import retrofit2.Retrofit

/**
 * Manual DI container. Instantiate once (e.g., in Application) and access its singletons.
 */
class AppContainer(context: Context, app: Application) {
    val application = app

    // Core singletons
    // Retrofit é criado de forma lazy e só será instanciado quando a API remota for ligada.
    private val retrofit: Retrofit by lazy { RetrofitClient.instance }

    private val database: EntradaLivreDatabase by lazy {
        Room.databaseBuilder(context.applicationContext, EntradaLivreDatabase::class.java, "entradalivre.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // DAOs
    private val establishmentDao: EstablishmentDao by lazy { database.establishmentDao() }
    private val reviewDao: ReviewDao by lazy { database.reviewDao() }
    private val userDao: UserDao by lazy { database.userDao() }

    // APIs (não utilizadas enquanto o app é offline)
    private val establishmentApi: EstablishmentApi by lazy { retrofit.create(EstablishmentApi::class.java) }
    private val reviewApi: ReviewApi by lazy { retrofit.create(ReviewApi::class.java) }
    private val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }

    // Data sources

    // local
    val establishmentLocalDataSource by lazy { EstablishmentLocalDataSource(establishmentDao) }
    val reviewLocalDataSource by lazy { ReviewLocalDataSource(reviewDao) }
    val userLocalDataSource by lazy { UserLocalDataSource(userDao) }
    val photoLocalDataSource by lazy { PhotoLocalDataSource(context.applicationContext) }

    // remote
    val establishmentRemoteDataSource by lazy { EstablishmentRemoteDataSource(establishmentApi) }
    val reviewRemoteDataSource by lazy { ReviewRemoteDataSource(reviewApi) }
    val userRemoteDataSource by lazy { UserRemoteDataSource(userApi) }

    // Repositories
    val establishmentRepository: EstablishmentRepository by lazy {
        EstablishmentRepositoryImpl(
            localDS = establishmentLocalDataSource,
            remoteDS = establishmentRemoteDataSource,
        )
    }
    val reviewRepository: ReviewRepository by lazy {
        ReviewRepositoryImpl(
            localDS = reviewLocalDataSource,
            photoDS = photoLocalDataSource,
            remoteDS = reviewRemoteDataSource,
        )
    }
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            localDS = userLocalDataSource,
            remoteDS = userRemoteDataSource,
        )
    }

    // Use cases
    val establishmentUseCases: EstablishmentUseCases by lazy {
        EstablishmentUseCases(
            getEstablishments = GetEstablishmentsUseCase(establishmentRepository),
            getEstablishmentById = GetEstablishmentByIdUseCase(establishmentRepository),
            searchEstablishments = SearchEstablishmentsUseCase(establishmentRepository),
            getFavorites = GetFavoriteEstablishmentsUseCase(establishmentRepository),
            toggleFavorite = ToggleFavoriteUseCase(establishmentRepository),
            shareEstablishment = ShareEstablishmentUseCase(establishmentRepository),
        )
    }

    val reviewUseCases: ReviewUseCases by lazy {
        ReviewUseCases(
            getRecentReviews = GetRecentReviewsUseCase(reviewRepository),
            getEstablishmentReviews = GetEstablishmentReviewsUseCase(reviewRepository),
            getUserReviews = GetUserReviewsUseCase(reviewRepository),
            submitReview = SubmitReviewUseCase(reviewRepository, establishmentRepository),
            toggleHelpful = ToggleReviewHelpfulUseCase(reviewRepository),
            savePhoto = SaveReviewPhotoUseCase(reviewRepository),
            deletePhoto = DeleteReviewPhotoUseCase(reviewRepository),
        )
    }

    val userUseCases: UserUseCases by lazy {
        UserUseCases(
            loginUser = LoginUserUseCase(userRepository),
            logoutUser = LogoutUserUseCase(userRepository),
            getLocalUser = GetLocalUserUseCase(userRepository),
            updateAccessibilityProfile = UpdateAccessibilityProfileUseCase(userRepository),
            updateUserName = UpdateUserNameUseCase(userRepository),
        )
    }

    // Preferences / DataStore
    val preferences: EntradaLivrePreferences by lazy { EntradaLivrePreferences(context.applicationContext) }
}
