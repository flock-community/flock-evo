//package community.flock
//
//import org.jetbrains.exposed.dao.UUIDEntity
//import org.jetbrains.exposed.dao.UUIDEntityClass
//import org.jetbrains.exposed.dao.id.EntityID
//import org.jetbrains.exposed.dao.id.UUIDTable
//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.transactions.transaction
//import java.util.UUID
//
//object GenerationsTable : UUIDTable() {
//  val simulationId = uuid(name = "simulationId").index()
//  val generationIndex = integer("generationIndex")
//  val worlds = reference("worldId", WorldsTable)
//}
//
//class GenerationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
//  companion object : UUIDEntityClass<GenerationEntity>(GenerationsTable)
//
//  var simulationId by GenerationsTable.simulationId
//  var generationIndex by GenerationsTable.generationIndex
//  val worlds by WorldEntity referrersOn  GenerationsTable.worlds
//}
//
//object WorldsTable : UUIDTable() {
//  var worldId = uuid(name = "worldId").autoGenerate()
//  var age = integer("age")
//}
//
//class WorldEntity(id: EntityID<UUID>) : UUIDEntity(id) {
//  companion object : UUIDEntityClass<WorldEntity>(WorldsTable)
//
//  var worldId by WorldsTable.worldId
//  var age by WorldsTable.age
//}
//
//fun saveGeneration(generationK: GenerationK) {
//  Database.connect("jdbc:h2:./db", driver = "org.h2.Driver")
//
//  transaction {
//    addLogger(StdOutSqlLogger)
//
//    SchemaUtils.create(GenerationsTable, WorldsTable)
//
//    var worlds = transaction {
//      generationK.worlds.map {
//        WorldEntity.new {
//          age = it.age
//          worldId = UUID.randomUUID()
//        }
//      }
//    }
//
//    val generationE: GenerationEntity = transaction {
//      GenerationEntity.new(UUID.randomUUID()) {
//        generationIndex = generationK.index
//        simulationId = generationK.simulationId
//        worlds = SizedCollection(worlds).toList()
//      }
//    }
//  }
//}
