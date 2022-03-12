package info.digitalpoet.auth.domain.repository

interface Repository<in ID, Entity>
{
    fun save(entity: Entity): Entity

    fun update(entity: Entity): Entity

    fun findById(id: ID): Entity

    fun delete(id: ID): Entity
}
