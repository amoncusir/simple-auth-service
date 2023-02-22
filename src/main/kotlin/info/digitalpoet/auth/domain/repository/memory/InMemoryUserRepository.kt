package info.digitalpoet.auth.domain.repository.memory

import info.digitalpoet.auth.domain.model.User
import info.digitalpoet.auth.domain.repository.NotFoundEntity
import info.digitalpoet.auth.domain.repository.UserRepository
import info.digitalpoet.auth.domain.values.Email
import info.digitalpoet.auth.domain.values.UserId

class InMemoryUserRepository(userList: List<User> = listOf()): UserRepository
{
    private val cache: HashMap<String, User> = userList
        .map { it.userId.toString() to it }
        .toMap(HashMap())

    override fun findUserByEmail(email: Email): User
    {
        return cache
            .entries
            .find { it.value.email == email }
            ?.value ?: throw NotFoundEntity(email.toString(), "User")
    }

    override fun save(entity: User): User
    {
        val copy = entity.copy()
        cache[copy.userId.toString()] = copy
        return copy
    }

    override fun update(entity: User): User
    {
        return save(entity)
    }

    override fun findById(id: UserId): User
    {
        return cache[id.toString()] ?: throw NotFoundEntity(id.toString(), "User")
    }

    override fun delete(id: UserId): User
    {
        return cache.remove(id.toString()) ?: throw NotFoundEntity(id.toString(), "User")
    }

    fun clear() {
        cache.clear()
    }
}
