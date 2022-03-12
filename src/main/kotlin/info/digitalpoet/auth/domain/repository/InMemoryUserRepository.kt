package info.digitalpoet.auth.domain.repository

import info.digitalpoet.auth.domain.model.User

class InMemoryUserRepository(userList: List<User>): UserRepository
{
    private val cache: HashMap<String, User> = userList
        .map { it.userId to it }
        .toMap(HashMap())

    override fun findUserByEmail(email: String): User
    {
        return cache
            .entries
            .find { it.value.email == email }
            ?.value ?: throw NotFoundEntity(email, "User")
    }

    override fun save(entity: User): User
    {
        val copy = entity.copy()
        cache[entity.userId] = copy
        return copy
    }

    override fun update(entity: User): User
    {
        return save(entity)
    }

    override fun findById(id: String): User
    {
        return cache[id] ?: throw NotFoundEntity(id, "User")
    }

    override fun delete(id: String): User
    {
        return cache.remove(id) ?: throw NotFoundEntity(id, "User")
    }
}
