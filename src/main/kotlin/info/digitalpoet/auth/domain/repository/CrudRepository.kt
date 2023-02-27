package info.digitalpoet.auth.domain.repository

interface CrudRepository<in ID, Entity>: Repository
{
    fun save(entity: Entity): Entity

    fun update(entity: Entity): Entity

    fun findById(id: ID): Entity

//    fun delete(id: ID): Entity
}
