#pragma once

// dfs_order.hpp

#include <vector>
#include <queue>
#include <deque>
#include "digraph.h"

/**
 * @brief Iterable queue datatype from https://stackoverflow.com/questions/1259099/stdqueue-iteration
 * 
 * @tparam T
 * @tparam Container=std::deque<T>  [description]
 */
template<typename T, typename Container=std::deque<T> >
class iterable_queue : public std::queue<T,Container>
{
public:
    typedef typename Container::iterator iterator;
    typedef typename Container::const_iterator const_iterator;

    iterator begin() { return this->c.begin(); }
    iterator end() { return this->c.end(); }
    const_iterator begin() const { return this->c.begin(); }
    const_iterator end() const { return this->c.end(); }

    typedef typename Container::reverse_iterator reverse_iterator;
    typedef typename Container::const_reverse_iterator const_reverse_iterator;

    reverse_iterator rbegin() { return this->c.rbegin(); }
    reverse_iterator rend() { return this->c.rend(); }
    const_reverse_iterator rbegin() const { return this->c.rbegin(); }
    const_reverse_iterator rend() const { return this->c.rend(); }
};

/**
 *  Class represents a data type for determining depth-first search ordering of the vertices
 *  in a digraph including preorder, postorder, and reverse postorder (topological order).
 */
class dfs_order
{
private:
	std::vector<bool> marked;              //< marked[v] = has v been marked in dfs?
  std::vector<int> pre;                  //< pre[v]    = preorder  number of v
  std::vector<int> post;                 //< post[v]   = postorder number of v
  iterable_queue<int> preorder;          //< vertices in preorder
  iterable_queue<int> postorder;         //< vertices in postorder
  iterable_queue<int> reverse_postorder; //< vertices in reverse postorder (topological order)
  int pre_counter;                       //< counter or preorder numbering
  int post_counter;                      //< counter for postorder numbering

  /**
   * @brief Validate vertex being in range [0, V-1]
   * 
   * @param v a vertex
   * throw std::invalid_argument exception unless v is valid
   */
  void validate_vertex (int v) const
    {
      int V = marked.size ();
      if (v < 0 || v >= V)
        throw std::invalid_argument ("Graph vertex must be in range [0, V-1].");
    }

  /**
   * Run recursive version of depth-first search algorithm
   * which constructs preorder and postorder of vertexes
   * @param G given digraph
   * @param v the source vertex
   */
  void dfs_recursive (const digraph &G, const int v)
    {
      marked[v] = true;

      // maintain preorder
      pre[v] = pre_counter++;
      preorder.push (v);

      // just run DFS
      for (auto w : G.adj (v))
        if (!marked[w])
            dfs_recursive (G, w);

      // maintain postorder
      post[v] = post_counter++;
      postorder.push (v);
    }

public:
  dfs_order () = delete;
  
  // Constructor
  dfs_order (const digraph &G) : marked (G.get_vertexex_num (), false), 
    pre (G.get_vertexex_num (), -1), post (G.get_vertexex_num (), -1)
    {
      // Construct preorder and postorder with dfs
      for (int v = 0; v < G.get_vertexex_num (); v++)
        if (!marked[v])
          dfs_recursive (G, v);

      // Construct reverse postorder (topological order)
      for (auto rit = postorder.rbegin (); rit != postorder.rend (); ++rit)
        {
          reverse_postorder.push (*rit);
        }
    }

  /**
   * Returns the preorder number of vertex v.
   * @param  v the vertex
   * @return the preorder number of vertex v
   * @throws std::invalid_argument exception unless v is valid
   */
  int get_preorder_ind (int v) const 
    {
      validate_vertex (v);
      return pre[v];
    }

  /**
   * Returns the postorder number of vertex v.
   * @param  v the vertex
   * @return the postorder number of vertex v
   * @throws std::invalid_argument exception unless v is valid
   */
  int get_postorder_ind (int v) const 
    {
      validate_vertex (v);
      return post[v];
    }

  /**
   * Returns the vertices in postorder.
   * @return the vertices in postorder, as a vector of vertices
   */
  iterable_queue<int> & get_postorder () 
    {
      return postorder;
    }

  /**
   * Returns the vertices in preorder.
   * @return the vertices in preorder, as a vector of vertices
   */
  iterable_queue<int> & get_preorder () 
    {
      return preorder;
    }


  /**
   * Returns the vertices in reverse postorder (or topological order).
   * @return the vertices in reverse postorder, as a vector of vertices
   */
  iterable_queue<int> & get_reverse_postorder () 
    {
      return reverse_postorder;
    }
};

