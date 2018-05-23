package me.tylerbwong.stack.data.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.tylerbwong.stack.data.model.Question
import me.tylerbwong.stack.data.model.Sort
import me.tylerbwong.stack.data.network.ServiceProvider
import me.tylerbwong.stack.data.persistence.StackDatabase
import me.tylerbwong.stack.data.persistence.entity.QuestionEntity
import me.tylerbwong.stack.data.persistence.entity.UserEntity
import me.tylerbwong.stack.data.toQuestion
import me.tylerbwong.stack.data.toQuestionEntity
import me.tylerbwong.stack.data.toUserEntity

class QuestionRepository(private val stackDatabase: StackDatabase) {

    private val questionDao by lazy { stackDatabase.getQuestionDao() }
    private val userDao by lazy { stackDatabase.getUserDao() }

    fun getQuestions(sort: String): Flowable<List<Question>> = Single.mergeDelayError(getQuestionsFromDb(sort), getQuestionsFromNetwork(sort)).subscribeOn(Schedulers.io())

    private fun getQuestionsFromDb(@Sort sort: String): Single<List<Question>> =
            questionDao.get(sort)
                    .subscribeOn(Schedulers.computation())
                    .map { questions ->
                        questions.map { question ->
                            question.toQuestion(userDao.get(question.owner), question.lastEditor?.let { userDao.get(it) })
                        }
                    }

    private fun getQuestionsFromNetwork(@Sort sort: String): Single<List<Question>> =
            ServiceProvider.questionService.getQuestions(sort = sort)
                    .map { it.items }
                    .flatMap { questions -> saveQuestions(questions, sort).toSingleDefault(questions) }

    private fun saveQuestions(questions: List<Question>, @Sort sortString: String): Completable =
            Completable.fromAction {
                val userEntities = mutableListOf<UserEntity>()
                val questionEntities = mutableListOf<QuestionEntity>()
                questions.forEach { question ->
                    userEntities.add(question.owner.toUserEntity())
                    question.lastEditor?.let { userEntities.add(it.toUserEntity()) }
                    questionEntities.add(question.toQuestionEntity(sortString))
                }
                questionDao.update(questionEntities, userEntities, sortString, userDao)
            }

}