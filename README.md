# rdf2pg
A java application to transform RDF databases into Property Graph databases.

Usage:

## Simple instance mapping

$ java -jar rdf2pg -sdm <RDF_filename>

## General database mapping (schema-independent)

$ java -jar rdf2pg -gdm <RDF_filename>

## Direct database mapping (schema-dependent)

$ java -jar rdf2pg -cdm <RDF_filename> <RDFS_filename>