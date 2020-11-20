package app.api.v1.pojos.mapping

import app.api.v1.pojos.SubjectPojo
import app.data.entities.SubjectEntity

fun SubjectEntity.mapToPojo() = SubjectPojo(id, name, teacher)

fun Collection<SubjectEntity>.mapToSubjectPojos() = map { it.mapToPojo() }