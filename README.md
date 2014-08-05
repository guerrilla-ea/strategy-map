#Strategy Exporter for Archi [http://www.archimatetool.com]

Strategy Exporter is a plug-in for the Archi Archimate Modeling tool that uses Archimate models to develop strategy visualizations called "Strategy Maps".

##Background
We define a Strategy Map as "A sequences series of interrelated activities and outcomes required to achieve a defined set of related objectives designed to influence and drive the business."
	*Matt Stuempfle presentation to the Open Group in 2013*

There are three elements of a Strategy Map
- Objectives: a high level statement that articulates *what* we want to *accomplish*.
  - Example: "I want to be physically fit before I go to the beach."
- Outcome: *measurable value* delivered and associated with an objective.
  - Example: "Lose 15 pounds in the next three months."
- Activity: the *actions* or *work* that we take to achieve an outcome. This is the "*how*" we get there.
  - Example: "Create an exercise plan."
  - Example: "Exercise 60 minutes today."
  
The Strategy Map is visualized through a directed graph that provides a non-biased view of the work required to achieve incremental value to the business within time-boxed periods taking into account the various inter-dependencies between goals and work efforts.

##Types of Exports
Strategy Exporter exports into the following formats:
- Strategy Map (.dot format) (see: http://graphviz.org for details)
- Objectives (.html format)
- Cyper Query Language (.cql) (see: http://neo4j.org for details)

##Making a Strategy Map
Strategy Map development makes use of the Motivation Extension to Archimate.
The Archimate concepts and relationships used in the development of a Strategy Map are:

- Goal (maps to an "outcome" from the Strategy Map definition)
- Work Package (maps to an "activity" from the Strategy Map definition)
- Realization Relationship

###Objectives and Goals
Objectives and goals form the foundation of the strategy map in terms of measurable business outcomes.
Objectives are denoted by folders in the Motivation model structure and represent a high-level notion of what the organization wants to accomplish.  Mapped to each driver are one or more Goals - or outcomes - against which we can measure business value.

Here's how you can add Goals and Objectives to your strategy map:
1. Start by creating a new model in Archi
2. Create a new View using the Project Viewpoint
3. Create any number of Goals in the viewpoint and assign both a name (short) and a description (more detailed).
4. In the Models window, under the Motivation folder, add sub-folders for each high-level Objective. Give the folder a short name along with a more detailed description.
5. Move the Goals into the folder of the Objective with which they are associated.
6. Repeat these steps. You can have more than one View for your Goals.

Tip: It often helps to have one View per Objective or even one View per Goal depending on how complex the effort is to attain the Goal or Objective.

Once you have done this you can then use the Objectives Exporter to export an HTML view of the Goals (i.e. outcomes) mapped to the Objectives from the Export Menu. See Objectives Export under the Other Exporters section for details.

###Relationships
There are multiple types of relationships used to indicate dependencies. These relationships are as follows:
- Work Package to Goal (common)
- Work Package to Work Package (common)
- Goal to Goal (rarer)

For the Strategy Map to be properly generated, use the following relationships with the connections in the direction as specified in the "From"-"To" columns.
|From|To|Relationship|
|----|--|------------|
|Work Package|Work Package|Flow Relationship|
|Work Package|Goal|Realization Relationship|
|Goal|Work Package|Association Relationship|
|Goal|Goal|Influence|

Note: you can move the elements and connectors around in the diagram to make it easier to place elements in complicated diagrams. However, our final output will be generated, so don't spend a lot of time making it look pretty. We also recommend breaking down your overall strategy map into different Views (i.e. diagrams) for ease of maintenance.

###Meta-Data and Properties
The first time you run one of the Strategy Map exporters, three properties will be automatically added to each element in your Archi model.

These properties may be found on the Properties section of the Properties window when you select a concept on a View or in the Models window.

The properties added are used for the following purposes:

|**Property**|**Purpose/Description**|
|-------|----------------|
|Status|Indicates which lifecycle stage a work package or goal is in. This can be used to filter in the Filder Dialog that is displayed when you run the exporter. This will also affect the color of the concept in the Strategy Map view when rendered. Lifecycle values (in order) are: Proposed (i.e. not official yet); Validated (i.e. official but not being worked); Approved (i.e. being worked); Implemented (i.e. work has been completed); Mandatory (i.e. being worked prior to prerequisites being met).|  
|Phase|Indicates which logical grouping (i.e. phase) a Goal is associated with. This only needs to be set for Goals. Use this to group Goals and their associated work into logial groups to visualize the work that needs to be completed to attain an outcome in a certain period of time. Can be any value. On the Strategy Map visualization, it will be written as "Phase *<your value>*").|
|Complexity|Indicates how complex a work package (i.e. activity) is. This will affect the size of the concept in the Strategy Map view when rendered. Values are: Easy, Medium, and Difficult.|

##Strategy Map Export
The Strategy Map export creates a .dot file from which you can render a visualization of the Strategy Map. To render, use graphviz (http://graphviz.org). From there you can create images of various formats.

When exporting the following points should be noted:
- Use of the filter dialog during the export will present different results be omitting or including map components. For example, if you are modifying an existing map and don't want to show the "unapproved" components, the component Status should be set to "Proposed" and then don't select "Proposed" in the export dialog. The map generated will ignore any components with the status of "Proposed". This can be a useful tool, but note that it will present drastically different results based on the filter criteria selected and the relationships of the components affected.
- The export creates a directed graph based on the components in your archimate model. All components of Work Packages and Goals are included irrespective of the View (i.e. diagram) or model tree structure.
- The export creates sub-graphs based on the Phase property. Use this to denote time-boxed groups of work (e.g. quarter, month) or other logical phase.
- Elements on the map are sized according to the Complexity property
- Elements on the map are color-coded based on the Status property. Use this to track progress against the map over time as well as highlight activities that are being executed without their dependencies being met.

##Other Exporters
There are additional exporters to allow advanced analysis and tools to be used on your Archi models.

###Cypher Query Language (CQL) Export
The CQL export is a format that allows you to take your Archi model and import via a script into a graph database like neo4j. Use the CQL file like any other to import your data into a neo4j instance and from there you can perform various types of analysis like:
- Dependency Analysis: which architecture components depend on others
- Impact Analysis: if I change component X, which other components may be impacted
- Composition Analysis: which technologies support a business process or business application

Note that the CQL exporter exports all components and relationships in your model to allow for the full analysis of your architecture.

###Objectives Export
The Objectives exporter takes the Objectives (as folders in the model view Motivation folder) and the Goals identified under the Objectives and extracts into a simple HTML table.
As with the Strategy Map exporter, you can filter based on the Status. The Description data for the Objectives Folders is used along with the Phase and Goal description.