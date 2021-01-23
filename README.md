# Que-backend

**Que is a tool that allows for even, fair and organized distribution of cyclic tasks among the members of a group.**

This implementation of project is made as a proof of concept for one use case specifically – to 
distribute taking notes in school evenly across the members of the class. Despite of that, the idea is 
more universal and so will be the final implementation of the project, which will allow it to support 
even more use cases, like household chores, sharing a single item (like computer) or sharing 
responsibilities to name a few.

The main idea behind the implementation is that the points of time in which something is happening
should be independent of the people assigned to these points in time. As a result of that, if the point
in time is drops no one misses his turn, and when the user drops the next user is assigned instead.

With this solution, each user has no interest of the state of the queue besides his turns, which allows
for swaps between users in the same queue or even between queues, which is already implemented.

Provided the proof-of-concept implementation will pass the tests and meet our expectations, we
seek to further develop and expand this idea which, we hope, will result in publicly available
universal solution. 
