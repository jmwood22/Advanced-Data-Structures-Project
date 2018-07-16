# Advanced-Data-Structures-Project
Final course project for a graduate level Advanced Data Structures Project.

We were tasked with implementing a system to find the n most popular hastags appearing on social media. For the scope
of the project we were given the hashtags in an input file. The basic idea for the implementation was to use a max priority 
structure to find out the most popular hashtags. 

A Max Fibonacci heap was implemented to keep track of the frequency of hashtags.

A Hash table was used where the key for the hash table was a hashtag and the value is a pointer to the corresponding node
in the Fibonacci heap.
