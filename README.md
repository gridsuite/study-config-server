# Study Config Server

[![Actions Status](https://github.com/gridsuite/study-config-server/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/gridsuite/study-config-server/actions)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=org.gridsuite%3Astudy-config-server&metric=coverage)](https://sonarcloud.io/component_measures?id=org.gridsuite%3Astudy-config-server&metric=coverage)
[![MPL-2.0 License](https://img.shields.io/badge/license-MPL_2.0-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)

## Description

The **study-config-server** is a microservice of the [GridSuite](https://github.com/gridsuite) platform dedicated to **storing GridStudy user-defined display and layout configurations**: spreadsheet configurations, network visualization parameters, diagram grid layouts, workspaces and computation result filters.

It provides the following capabilities:

- **Manage spreadsheet configurations**: create, duplicate, update, rename and delete configurations describing a set of **columns** (formulas, filters, sort) used to display equipment data in tabular form, including column-level CRUD, reordering, states and global/column filters.
- **Manage spreadsheet configuration collections**: group several spreadsheet configurations together (create, duplicate, merge, append, reorder, replace), and create a default collection.
- **Manage network visualization parameters**: display settings for the network map, single-line diagrams and network area diagrams (create, duplicate, update, delete).
- **Manage diagram grid layouts**: the positioning of diagrams (map, substation, voltage level, network area diagram) within a study's diagram grid.
- **Manage workspaces and workspace configs**: standalone workspaces and named collections of workspaces (workspaces-configs), including their panels (single-line diagram, network area diagram panels) and per-panel state such as the current NAD (Network Area Diagram) configuration.
- **Manage computation result filters**: global and per-column filters applied to computation result tables, scoped by computation type and sub-type.

---

## Technical Stack

- Spring Boot (Web, Data JPA, Actuator)
- PostgreSQL
- Liquibase
- API documentation: OpenAPI / Swagger (`springdoc`)
- [gridsuite-ws-commons](https://github.com/gridsuite/powsybl-ws-commons)

---

## Development Scripts

Build Docker image

```shell
mvn install -DskipTests -Dpowsybl.docker.install
```

Please read [liquibase usage](https://github.com/powsybl/powsybl-parent/#liquibase-usage) for instructions to automatically generate changesets. After you generated a changeset do not forget to add it to git and in `src/main/resources/db/changelog/db.changelog-master.yaml`.

---

## Interactions with Other Microservices

```text
┌──────────────────────────┐
│  study-config-server     │──► single-line-diagram-server   (resolve diagram-related data for NAD configurations)
└──────────────────────────┘
```

---

