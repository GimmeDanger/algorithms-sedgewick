#pragma once

// depth_first_search.hpp

#include <vector>  // for inner depth_first_search structures
#include <stack>   // for nonrecursive implementation of dfs
#include "graph.h" // for graph api

/**
 * DFS algorithm finds vertexes connected to the source vertex s
 */
class depth_first_search
{
private:
	std::vector<bool> marked;
  std::vector<int> edge_to;
  int count = 0;

  /**
   * Run recursive version of depth-first search algorithm
   * @param G given undirected graph
   * @param v the source vertex
   */
  void dfs_recursive (const graph &G, const int v)
    {
      count++;
      marked[v] = true;
      for (auto w : G.adj (v))
        {
          if (!marked[w])
            {
              dfs_recursive (G, w);
              edge_to[w] = v;
            }
        }
    }

  /**
   * Run nonrecursive version of depth-first search algorithm
   * @param G given undirected graph
   * @param v the source vertex
   */
  void dfs_nonrecursive (const graph &G, const int v)
    {
      std::stack<int> q;
      q.push (v);
      count++;
      marked[v] = true;
      while (!q.empty ())
        {
          int q_top = q.top (); q.pop ();
          for (auto w : G.adj (q_top))
            {
              if (!marked[w])
                {
                  q.push (w);
                  count++;
                  marked[w] = true;
                  edge_to[w] = q_top;
                }
            }          
        }
    }

public:
  depth_first_search () = delete;
  
  depth_first_search (const graph &G, const int s, const bool use_recursive_dfs) : 
    marked (G.get_vertexex_num (), false), edge_to (G.get_vertexex_num (), -1)
    {
      count = 0;
      if (use_recursive_dfs)
        dfs_recursive (G, s);
      else
        dfs_nonrecursive (G, s);
    }

  /**
   * Is there a path between the source vertex s and vertex v?
   * @param v the vertex
   * @return true if there is a path
   */
  bool has_path_to (int v)
    {
      return marked[v];
    }

  /**
   * Returns a number of vertexes connected to the source vertex s
   * @return a number of vertexes connected to the source vertex s
   */
  int connected_vertexes ()
    {
      return count;
    } 

};

