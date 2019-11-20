package org.yoregs.machine.domain

interface Endpoint
interface Role
interface Choice
interface Variable

class LinearVariable : Variable

interface InternalChoice<T> : Endpoint
interface ExternalChoice<T> : Endpoint
interface Lollipop<T> : Endpoint
interface Tensor<T> : Endpoint
