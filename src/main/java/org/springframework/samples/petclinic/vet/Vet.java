/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Person;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Simple JavaBean domain object representing a veterinarian.
 * This entity class inherits basic identity and naming properties from {@link Person} 
 * and introduces a many-to-many relationship mapping for a veterinarian's {@link Specialty}.
 * It safely manages its internal set of specialties and exposes a sorted, 
 * unmodifiable list for external retrieval, ensuring state consistency.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Arjen Poutsma
 */
@Entity
@Table(name = "vets")
public class Vet extends Person {

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"),
			inverseJoinColumns = @JoinColumn(name = "specialty_id"))
	private Set<Specialty> specialties;

	/**
	 * Provides access to the internal set of specialties.
	 * Initializes the set if it is currently null to avoid NullPointerExceptions.
	 *
	 * @return the internal mutable set of {@link Specialty}
	 */
	protected Set<Specialty> getSpecialtiesInternal() {
		if (this.specialties == null) {
			this.specialties = new HashSet<>();
		}
		return this.specialties;
	}

	/**
	 * Retrieves the list of specialties sorted by their name.
	 * This method relies on the internal set while exposing a structured list.
	 *
	 * @return a sorted {@link List} of {@link Specialty} objects assigned to this vet
	 */
	@XmlElement
	public List<Specialty> getSpecialties() {
		return getSpecialtiesInternal().stream()
			.sorted(Comparator.comparing(NamedEntity::getName))
			.collect(Collectors.toList());
	}

	/**
	 * Returns the total number of specialties this veterinarian possesses.
	 *
	 * @return the count of specialties
	 */
	public int getNrOfSpecialties() {
		return getSpecialtiesInternal().size();
	}

	/**
	 * Adds a new specialty to this veterinarian's set of specialties.
	 *
	 * @param specialty the {@link Specialty} to add
	 */
	public void addSpecialty(Specialty specialty) {
		getSpecialtiesInternal().add(specialty);
	}

}
