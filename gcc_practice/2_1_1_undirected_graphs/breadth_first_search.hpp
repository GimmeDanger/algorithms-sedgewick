#pragma once

// breadth_first_search.hpp

#include <vector>  // for inner breadth_first_search structures
#include <queue>   // for implementation of bfs
#include <limits>  // for infinity
#include "graph.h" // for graph api

/**
 * BFS algorithm finds shortest paths from set of source vertexes
 */
class breadth_first_search
{
private:
	std::vector<bool> marked; // marked[v] = is there an s-v path
  std::vector<int> edge_to; // edge_to[v] = previous edge on shortest s-v path
  std::vector<int> dist_to; // dist_to[v] = number of edges shortest s-v path

  /**
   * Run of breadth-first search algorithm for single source vertex
   * @param G given undirected graph
   * @param v the source vertex
   */
  void bfs (const graph &G, const int s)
    {
      std::queue<int> q;      
      marked[s] = true;
      dist_to[s] = 0;
      q.push (s);      

      while (!q.empty ())
        {
          int q_front = q.front (); q.pop ();
          for (auto w : G.adj (q_front))
            {
              if (!marked[w])
                {                  
                  marked[w] = true;
                  edge_to[w] = q_front;
                  dist_to[w] = dist_to[q_front] + 1;
                  q.push (w);
                }
            }          
        }
    }

  /**
   * Run of breadth-first search algorithm for multiple source vertexes
   * @param G given undirected graph
   * @param v the source vertex
   */
  void bfs (const graph &G, const std::vector<int> &s)
    {
      std::queue<int> q;
      for (auto v : s)
        {          
          marked[v] = true;
          dist_to[v] = 0;
          q.push (v);
        }
      
      while (!q.empty ())
        {
          int q_front = q.front (); q.pop ();
          for (auto w : G.adj (q_front))
            {
              if (!marked[w])
                {
                  marked[w] = true;
                  edge_to[w] = q_front;
                  dist_to[w] = dist_to[q_front] + 1;
                  q.push (w);
                }
            }
        }
    }

public:
  breadth_first_search () = delete;
  
  // breadth-first search from single source
  breadth_first_search (const graph &G, const int s) : 
    marked (G.get_vertexex_num (), false),
    edge_to (G.get_vertexex_num (), -1),
    dist_to (G.get_vertexex_num (), std::numeric_limits<int>::infinity())
    {
      bfs (G, s);
    }

  // breadth-first search from multiple sources
  breadth_first_search (const graph &G, const std::vector<int> &s) : 
    marked (G.get_vertexex_num (), false),
    edge_to (G.get_vertexex_num (), -1),
    dist_to (G.get_vertexex_num (), std::numeric_limits<int>::infinity())
    {
      bfs (G, s);
    }

  /**
   * Is there a path between the source vertex s and vertex v?
   * @param v the vertex
   * @return true if there is a path
   */
  bool has_path_to (int v) const
    {
      return marked[v];
    }

  int distance_to (int v) const
    {
      return dist_to[v];
    }
};

