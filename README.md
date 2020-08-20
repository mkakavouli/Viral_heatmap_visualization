# Viral_heatmap_visualization

Viral heatmap visualization is a JAVA desktop application in which the viral genome consensus sequences will be visualised in a heatmap-like plot based on the visualization used in Maclean et al, 2020 created by Dr Richard Orton. 

Such a plot allows the user to easily see what mutations a sample contains, whether the mutations are synonymous or non-synonymous, and to examine and compare the mutation patterns between samples.

Information about the frequency of mutations, the cooccurrence of genetic variants and the relation between samples are easily visible from the generated heatmaps.

## Input
The input file is tab delimited text file created from a python script called ‘valign_mutations_dnds.py’ (https://github.com/rjorton/VAlign; created by Dr Richard Orton), 
which takes as input an alignment of complete viral genome sequences and a text file of ORF coordinates, and outputs details of all the mutations present in all the samples

## Feautures
 ### Search options:
- Search and highlight one or more samples
- Search and highlight one or more nucleotide position
 ### Filtering options:
- Filter out the samples with less than a minimum number of mutations (set by the user)
- Filter out the genomic positions having less than a minimum number of mutations (set by the user)
- Filter out the samples having a mutation which is present in less than a minimum number of samples (set by the user)
- Display only the samples having the selected types of mutations (non-synonymous mutation, synonymous mutation, deletions, insertions, non-coding mutations)
- Display only the samples collected in the selected date range
- Display only the samples of specific global or U.K. virus lineage code
- Display only the samples collected from specific countries or regions of countries
### Customization options:
- Change the colors used to show mutations
- Change the size of the squares of the heatmap plot
- Change the order of samples being displayed (simple hierarchical ordering, date ordering [ascending], no ordering)
- Display labels demonstrating the names of the nucleotide positions/AA positions/ORF name of the most common mutation sites (> 10 samples)
### Save options:
- Save the heatmap plot as a PNG/JPEG/PDF file
