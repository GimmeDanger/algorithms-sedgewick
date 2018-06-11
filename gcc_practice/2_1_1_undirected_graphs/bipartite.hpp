#pragma once

// bipartite.hpp

#include <vector>  // for inner bipartite structures
#include <stack>   // for nonrecursive implementation of dfs
#include "graph.h" // for graph api

/**
 *  Find if the given undirected graph is bipartite and its division into two biparts (U, V)
 *       or odd cycle if it is not bipartite
 */
class bipartite
{
private:
  bool is_bip = true;
  std::vector<int> odd_cycle;
  std::vector<int> edge_to;
  std::vector<int> marked; //< possible values are {-1, 0, 1 }  
  std::vector<int> U;      //< first  part of G
  std::vector<int> V;      //< second part of G 

  /**
   * Run nonrecursive version of depth-first search algorithm
   * @param G given undirected graph
   * @param v the source vertex
   */
  void dfs_nonrecursive (const graph &G, const int v)
    {
      std::stack<int> q;
      q.push (v);
      marked[v] = 1;
      V.push_back (v);
      while (!q.empty ())
        {
          int q_top = q.top (); q.pop ();
          for (auto w : G.adj (q_top))
            {              
              if (!marked[w])
                {                    
                  q.push (w);
                  // color in different color
                  marked[w] = marked[q_top] * (-1);
                  edge_to[w] = q_top;
                  // add w to appropriate bipart
                  if (marked[w] < 0) 
                    U.push_back (w);
                  else if (marked[w] > 0)
                    V.push_back (w);
                }
              else if (marked[w] == marked[q_top])
                {
                  is_bip = false;
                  U.clear (); V.clear ();
                                  
                  int common_ancestor = edge_to[w];
                  odd_cycle.push_back (common_ancestor);
                  odd_cycle.push_back (w);                  
                  for (int x = q_top; x != common_ancestor; x = edge_to[x])
                    odd_cycle.push_back (x);

                  return;
                }
            }          
        }
    }

public:
  bipartite () = delete;
  
  bipartite (const graph &G) : marked (G.get_vertexex_num (), 0), edge_to (G.get_vertexex_num (), -1)
    {
      is_bip = true;
      for (int v = 0; v < G.get_vertexex_num (); v++)
        {
          if (is_bip && !marked[v])
            {
              dfs_nonrecursive (G, v);
            }
        }
    }

  bool is_bipartite () const { return is_bip; }

  int get_color (int v) const { return is_bip ? marked[v] : 0; } 

  std::vector<int> & get_V () { return V; }

  std::vector<int> & get_U () { return U; }

  std::vector<int> & get_odd_cycle () { return odd_cycle; }
};

