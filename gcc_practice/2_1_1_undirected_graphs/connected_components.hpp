#pragma once

// connected_components.hpp

#include <vector>  // for inner cc structures
#include <stack>   // for nonrecursive implementation of dfs
#include "graph.h" // for graph api

/**
 *  Find all connected components in undirected graph by nonrecursive DFS algorithm
 */
class connected_components
{
private:
	std::vector<int> marked;
  std::vector<std::vector<int> >  cc;

  /**
   * Run nonrecursive version of depth-first search algorithm 
   *      which marks vertexes connected to v with cc_num label
   * @param G given undirected graph
   * @param v the source vertex
   * @param cc_num the number of current connected component
   */
  void dfs_nonrecursive (const graph &G, const int v, const int cc_num)
    {
      std::stack<int> q;
      q.push (v);      
      marked[v] = cc_num;
      cc[cc_num].push_back (v);
      while (!q.empty ())
        {
          int q_top = q.top (); q.pop ();
          for (auto w : G.adj (q_top))
            {
              if (marked[w] < 0)
                {
                  q.push (w);
                  marked[w] = cc_num;
                  cc[cc_num].push_back (w);
                }
            }
        }
    }

public:
  connected_components () = delete;
  
  connected_components (const graph &G) :  marked (G.get_vertexex_num (), -1)
    {
      for (int v = 0, cc_num = 0; v < G.get_vertexex_num (); v++)
        {
          if (marked[v] < 0) //< if v was not visited
            {
              cc.push_back (std::vector<int> ()); //< init current cc
              dfs_nonrecursive (G, v, cc_num); // find vertexes in current cc
              cc_num++;
            }
        }
    }

  /**
   * Are two vertexes v and w connected?
   * @param v the vertex
   * @param w the vertex
   * @return true if v and w belong to the same connected component
   */
  bool are_connected (int v, int w) const
    {
      return marked[v] == marked[w];
    }

  /**
   * Returns a number of connected components
   * @return a number of connected components
   */
  int get_connected_components_num () const
    {
      return cc.size ();
    }

  /**
   * Returns an id of connected component which contains a given vertex
   * @param v the vertex
   * @return an id of connected component which contains a given vertex
   */
  int get_connected_component_id (int v) const
    {
      return marked[v];
    }

  /**
   * Returns connected component of v
   * @param v the vertex
   * @return vertexes of connected component which contains v
   */
  std::vector<int> & get_connected_component (int v)
    {
      return cc[get_connected_component_id (v)];
    }
};

