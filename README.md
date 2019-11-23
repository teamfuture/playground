# playground

A very basic project for experimenting with code and concepts

## Basic HTTP I/O
Takes on the HTTP messaging part of the framework, WOAdaptor, WOMessage (and subclasses)… Might not sound too sexy, but important to get right.

## KVC

## Request handlers
From a design standpoint, development on request handlers can live in a separate stage, not really related to the HTTP layer A request handler is, after all, just kind of like a function—something that accepts parameters and returns known results. The request doesn't even have to be initiated by HTTP, and I, for one, would be very happy about creating a testable framework where we don't need an HTTP stack to test the results from a request handler :)

### Component Action request handler
I *think* these will be most complicated of the whole bunch.

### Direct Action request handler
This one should be easy

### WOResourceRequestHandler
Resource caching, layout of resources within project etc.

## Templating
WODynamicElement, WOComponent, WOAssociation et al.

## Generic tags
Implementation of the basic HTML elements (input,form,img,a etc.)

## Interop with the deployment environment
So… Not even sure if we want this one, I'm hopelessly out of date in deployment techniques. But including it for completeness

## IDE plugin work
Yeah. That's gonna be fun
