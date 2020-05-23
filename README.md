# rdf2pg
A java application to transform RDF databases into Property Graph databases.

Usage:

## Simple instance mapping

$ java -jar rdf2pg -sdm <RDF_filename>

## General database mapping (schema-independent)

$ java -jar rdf2pg -gdm <RDF_filename>

## Direct database mapping (schema-dependent)

$ java -jar rdf2pg -cdm <RDF_filename> <RDFS_filename>

## Output

The output of the rdf2pg is a property graph encoded using the PGO data format (https://github.com/lszeremeta/yarspg).

The rdf2pg API includes an interface named PGWriter which can be implemented to support other data formats. The use of PGWriter is very simple as it provides the methods WriteNode(PGNode node) and WriteEdge(PGEdge edge) which should be implemented with the corresponding instructions to write nodes and edges in the output file.
