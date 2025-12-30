package com.example.wedding_planner.di

import com.example.wedding_planner.data.repo.AuthRepository
import com.example.wedding_planner.data.repo.GuestRepository
import com.example.wedding_planner.data.repo.OrganizationRepository
import com.example.wedding_planner.data.repo.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepo(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = AuthRepository(auth, firestore)

    @Provides
    @Singleton
    fun provideOrgRepo(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ) = OrganizationRepository(firestore, auth)

    @Provides
    @Singleton
    fun provideGuestRepo(firestore: FirebaseFirestore) = GuestRepository(firestore)

    @Provides
    @Singleton
    fun taskRepo(firestore: FirebaseFirestore) = TaskRepository(firestore)
}